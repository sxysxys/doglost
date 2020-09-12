package com.shen.baidu.doglost.ui.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.bean.DogCurrentInfo;
import com.shen.baidu.doglost.presenter.INetPresenter;
import com.shen.baidu.doglost.presenter.impl.NetPresenterImpl;
import com.shen.baidu.doglost.utils.LogUtils;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.INetCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图定位。
 */
public class MapDemo extends Activity implements SensorEventListener, INetCallBack {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private static final int accuracyCircleFillColor = 0xAAFFFF88;
	private static final int accuracyCircleStrokeColor = 0xAA00FF00;
	private SensorManager mSensorManager;
	private Double lastX = 0.0;
	private int mCurrentDirection = 0;
	private double mCurrentLat = 0.0;
	private double mCurrentLon = 0.0;
	private float mCurrentAccracy;

	boolean isFirstStart = true;
	boolean isOpen = false;

	BaiduMap mBaiduMap;

	@BindView(R.id.bmapView)
	MapView mMapView;

	@BindView(R.id.button1)
	Button requestLocButton;

	@BindView(R.id.button_open)
	Button searchButton;

//	@BindView(R.id.button_close)
//	Button searchClose;

	@BindView(R.id.radioGroup)
	RadioGroup mGroup;


	boolean isFirstLoc = true; // 是否首次定位
	private MyLocationData locData;
	private float direction;

	private INetPresenter presenter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
		mCurrentMode = LocationMode.NORMAL;
		initView();
		initListener();
		initLocation();
		initPresenter();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		ButterKnife.bind(this);
		mBaiduMap = mMapView.getMap();
		requestLocButton.setText("普通");
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
	}

	private void initPresenter() {
		presenter = NetPresenterImpl.getInstance();
		presenter.registerCallback(this);
	}

	private void initListener() {
		requestLocButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
					case NORMAL:
						requestLocButton.setText("跟随");
						mCurrentMode = LocationMode.FOLLOWING;
						mBaiduMap.setMyLocationConfiguration(
								new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
						MapStatus.Builder builder = new MapStatus.Builder();
						builder.overlook(0);
						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
						break;
					case COMPASS:
						requestLocButton.setText("普通");
						mCurrentMode = LocationMode.NORMAL;
						mBaiduMap.setMyLocationConfiguration(
								new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
						MapStatus.Builder builder1 = new MapStatus.Builder();
						builder1.overlook(0);
						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
						break;
					case FOLLOWING:
						requestLocButton.setText("罗盘");
						mCurrentMode = LocationMode.COMPASS;
						mBaiduMap.setMyLocationConfiguration(
								new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
						break;
					default:
						break;
				}
			}
		});

//		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				if (checkedId == R.id.defaulticon) {
//					// 传入null则，恢复默认图标
//					mCurrentMarker = null;
//					mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
//				}
//				if (checkedId == R.id.customicon) {
//					// 修改为自定义marker
//					mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
//					mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker,
//							accuracyCircleFillColor, accuracyCircleStrokeColor));
//				}
//			}
//		});


		mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				//点击地图某个位置获取经纬度latLng.latitude、latLng.longitude
			}

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				//点击地图上的poi图标获取描述信息：mapPoi.getName()，经纬度：mapPoi.getPosition()
				return false;
			}
		});

		searchButton.setOnClickListener(v -> {
			if (presenter != null) {
				if (isOpen) {
					presenter.delConnect();
					isOpen = false;
					searchButton.setText("打开寻狗");
				} else {
					if (isFirstStart) {
						presenter.firstConnect();
						isFirstStart = false;
					} else {
						presenter.connect();
					}
					searchButton.setText("关闭寻狗");
					isOpen = true;
				}
			}
		});
	}

	/**
	 * 将定位的一些参数进行设置。
	 */
	private void initLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 需要在室外定位。
		option.setScanSpan(1000);
		//option.setNeedDeviceDirect(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}


	/**
	 * 这个负责设置转向的。
	 * @param sensorEvent
	 */
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		//每次方向改变，重新给地图设置定位数据，用上一次onReceiveLocation得到的经纬度、精度
		double x = sensorEvent.values[SensorManager.DATA_X];
		if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
			mCurrentDirection = (int) x;
			locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
			mBaiduMap.setMyLocationData(locData);
			
		}
		lastX = x;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	/**
	 * 当数据来了就回调，在这里把小狗的位置实时的更新，并判断是否在圈/电子围栏内。
	 * @param dogInfo
	 */
	@Override
	public void onNetDataLoaded(DogCurrentInfo dogInfo) {
		LogUtils.d(this, "数据加载成功");
		// 实时更新

	}

	/**
	 * 网络错误，或者长时间不喂狗。
	 */
	@Override
	public void onNetError() {
		ToastUtils.showToast("网络错误，未能连接上服务器");
	}

	/**
	 * 与服务器连接成功的回调
	 */
	@Override
	public void onNetSuccess() {
		ToastUtils.showToast("连接服务器成功");
	}

	/**
	 * 心跳回调。
	 */
	@Override
	public void heartBeatLoaded() {

	}

	/**
	 * 加载中。
	 */
	@Override
	public void loading() {

	}

	/**
	 * 正常的断开连接
	 */
	@Override
	public void onConnectQuit() {

	}

	/**
	 * 定位SDK监听函数，在设定时间来一次（1s）
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			mCurrentLat = location.getLatitude();
			mCurrentLon = location.getLongitude();
			mCurrentAccracy = location.getRadius();
			locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mCurrentDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			// 首次定位的时候，需要放大以观察位置。
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(18.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
			}
		}

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		// 为系统的方向传感器注册监听器
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onStop() {
		// 取消注册传感器监听
		mSensorManager.unregisterListener(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.unRegisterLocationListener(myListener);
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		presenter.unregisterCallback(this);
		super.onDestroy();
	}

}
