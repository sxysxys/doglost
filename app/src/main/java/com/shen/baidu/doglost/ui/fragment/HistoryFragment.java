package com.shen.baidu.doglost.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.shen.baidu.doglost.DemoApplication;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.constant.Const;
import com.shen.baidu.doglost.model.domain.HistoryPoint;
import com.shen.baidu.doglost.presenter.IHistoryPresenter;
import com.shen.baidu.doglost.presenter.impl.HistoryPresenterImpl;
import com.shen.baidu.doglost.ui.dialog.SelectTimeDialog;
import com.shen.baidu.doglost.utils.LogUtils;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.IHistoryCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends Fragment implements IHistoryCallback {

    private IHistoryPresenter mHistoryTracePresenter;
    private Unbinder mBind;

    @BindView(R.id.track_query_mapView)
    TextureMapView mBaiduView;

    @BindView(R.id.btn_create)
    Button buttonQuery;

    @BindView(R.id.btn_clear)
    Button buttonClear;

    private BaiduMap mBaiduMap;

    BitmapDescriptor startBD = BitmapDescriptorFactory
            .fromResource(R.drawable.ic_me_history_startpoint);
    BitmapDescriptor finishBD = BitmapDescriptorFactory
            .fromResource(R.drawable.ic_me_history_finishpoint);
    private SelectTimeDialog.Callback mSelectTimeCallback;
    private SelectTimeDialog mSelectTimeDialog;
    private MapStatus.Builder builder;

    // 地图显示在轨迹的中心处
    LatLng target;
    // 轨迹的所有点
    List<LatLng> latLngs = new ArrayList<>();
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Polyline mPolyline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);
        mBind = ButterKnife.bind(this, view);
        initView();
        initPresenter();
        initListener();
        return view;
    }

    private void initView() {
        buttonQuery.setText("查询轨迹");
        buttonClear.setText("清除轨迹");
        mBaiduMap = mBaiduView.getMap();
        // 开启定位图层。
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void initPresenter() {
        mHistoryTracePresenter = HistoryPresenterImpl.getInstance();
        mHistoryTracePresenter.registerCallback(this);
    }

    private void initListener() {
        mSelectTimeCallback = new SelectTimeDialog.Callback() {
            // 点击确定
            @Override
            public void onSureCallback(String startTime, String endTime) {
                // 此时需要通过这个去请求接口获取轨迹
                mHistoryTracePresenter.getHisPoint(Const.deviceId, startTime, endTime);
            }
            // 点击取消
            @Override
            public void onCancelCallback() {

            }
        };

        // 点击后开启dialog，回调相应的数据
        buttonQuery.setOnClickListener(v -> {
            if (mSelectTimeDialog == null) {
                mSelectTimeDialog = new SelectTimeDialog(getContext(), mSelectTimeCallback);
            }
            mSelectTimeDialog.show();
        });

        buttonClear.setOnClickListener(v -> {
            clearTrack();
        });
    }

    private void clearTrack() {
        mBaiduMap.clear();
        target = null;
        if (mPolyline != null) {
            mPolyline.remove();
        }
        if (mMarkerA != null && mMarkerB != null) {
            mMarkerA.remove();
            mMarkerB.remove();
        }
        latLngs.clear();
    }

    /**
     * 加载数据回来。
     * @param data
     */
    @Override
    public void onNetDataLoaded(HistoryPoint data) {
        List<HistoryPoint.DataBean> dataBeans = data.getData();
        // 先清除原先的轨迹
        clearTrack();
        // 处理生成点
        handlerPoint(dataBeans);
        // 画点
        drawTrack();
    }

    private void handlerPoint(List<HistoryPoint.DataBean> dataBeans) {
        double lanSum = 0;
        double latSum = 0;
        for (HistoryPoint.DataBean dataBean : dataBeans) {
            float latitude = dataBean.getLatitude();
            float longitude = dataBean.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            latLngs.add(latLng);
            latSum += latitude;
            lanSum += longitude;
        }
        target = new LatLng(latSum / latLngs.size(), lanSum / latLngs.size());
    }

    /**
     * 将此时的点都画出来
     */
    private void drawTrack() {
        /**
         * target代表了此时的需要放大显示的大致位置，target是所有点的中点。
         */
        builder = new MapStatus.Builder();
        builder.target(target).zoom(18f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        // 添加起点标记
        MarkerOptions oStart = new MarkerOptions();//地图标记覆盖物参数配置类
        oStart.position(latLngs.get(0));//覆盖物位置点，第一个点为起点
        oStart.icon(startBD);//设置覆盖物图片
        oStart.zIndex(1);//设置覆盖物Index
        mMarkerA = (Marker) (mBaiduMap.addOverlay(oStart)); //在地图上添加此图层

        //添加终点标记，标记为第二个
        MarkerOptions oFinish = new MarkerOptions().position(latLngs.get(latLngs.size()-1)).icon(finishBD).zIndex(2);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(oFinish));

        /**
         * 将轨迹画出来，将轨迹标记为第3个。
         */
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(3);
    }

    @Override
    public void onDataEmpty() {
        ToastUtils.showToast("未能找到此时间段的轨迹");
    }

    @Override
    public void onNetError() {
        ToastUtils.showToast("获取数据失败，网络错误");
    }

    @Override
    public void loading() {
        LogUtils.d(this, "正在加载中");
    }

    @Override
    public void onNetSuccess() {
        LogUtils.d(this, "连接成功...");
    }

//    /**
//     * 返回键的事件
//     * @param view
//     */
//    public void onBack(View view) {
//        super.onBackPressed();
//    }

    @Override
    public void onPause() {
        mBaiduView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mBaiduView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mBaiduView.getMap().clear();
        mBaiduView.onDestroy();
        mBaiduView = null;
        startBD.recycle();
        finishBD.recycle();
        mHistoryTracePresenter.unregisterCallback(this);
        mBind.unbind();
        super.onDestroy();
    }

}