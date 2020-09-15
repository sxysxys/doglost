package com.shen.baidu.doglost.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.shen.baidu.doglost.DemoApplication;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.model.domain.DogCurrentInfo;
import com.shen.baidu.doglost.constant.Const;
import com.shen.baidu.doglost.constant.FenceShape;
import com.shen.baidu.doglost.presenter.INetPresenter;
import com.shen.baidu.doglost.presenter.impl.NetPresenterImpl;
import com.shen.baidu.doglost.ui.dialog.FenceCreateDialog;
import com.shen.baidu.doglost.utils.BitmapUtil;
import com.shen.baidu.doglost.utils.LogUtils;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.INetCallBack;
import com.shen.baidu.mapapi.overlayutil.WalkingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图定位。
 */
public class MapDemo extends Activity implements SensorEventListener, INetCallBack, BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
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

	@BindView(R.id.btn_create)
	Button createButton;

	@BindView(R.id.btn_clear)
	Button clearButton;

	@BindView(R.id.button_lock)
	ImageView buttonLock;

	@BindView(R.id.button_search)
	Button buttonSearch;


	private DemoApplication mApplication;


	boolean isFirstLoc = true; // 是否首次定位
	private MyLocationData locData;
	private float direction;

	private INetPresenter presenter;
	private FenceShape fenceShape = FenceShape.circle;
	private int mVertexesNumber;
	// 设置圆形坐标的原点
	private LatLng circleCenter;
	// 设置多边形的几个顶点
	private List<LatLng> mapVertexes = new ArrayList<>();//顶点坐标（地图坐标类型）
	// 此时在地图上画了几个点了。
	private int vertexIndex = 0;
	private FenceCreateDialog fenceCreateDialog;
	private FenceCreateDialog.Callback createCallback;
	private double radius = 0;

	private boolean isDraw = false;
	private Marker curMarker;
	private RoutePlanSearch mRouteSearch;
//	private LatLng mCurDogPosition = new LatLng(31.83, 117.2);
	private LatLng mCurDogPosition;
	private WalkingRouteOverlay mSearchOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
		mCurrentMode = LocationMode.NORMAL;
		mApplication = (DemoApplication) getApplication();
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
		requestLocButton.setText("普通模式");
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
						requestLocButton.setText("跟随模式");
						mCurrentMode = LocationMode.FOLLOWING;
						mBaiduMap.setMyLocationConfiguration(
								new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
						MapStatus.Builder builder = new MapStatus.Builder();
						builder.overlook(0);
						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
						break;
					case COMPASS:
						requestLocButton.setText("普通模式");
						mCurrentMode = LocationMode.NORMAL;
						mBaiduMap.setMyLocationConfiguration(
								new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
						MapStatus.Builder builder1 = new MapStatus.Builder();
						builder1.overlook(0);
						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
						break;
					case FOLLOWING:
						requestLocButton.setText("罗盘模式");
						mCurrentMode = LocationMode.COMPASS;
						mBaiduMap.setMyLocationConfiguration(
								new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
						break;
					default:
						break;
				}
			}
		});


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

		/**
		 * 点击创建按钮，跳转后将数据带回来。
		 */
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDraw) {
					Intent intent = new Intent(MapDemo.this,CreateFenceOptions.class);
					startActivityForResult(intent, Const.REQUEST_CODE);
				} else {
					ToastUtils.showToast("请先清空围栏");
				}
			}
		});

		clearButton.setOnClickListener(v -> {
			resetStatus();
		});
		/**
		*   创建围栏的回调，也就是将图形在图中画出来或者取消。
		*/
		createCallback = new FenceCreateDialog.Callback() {

			@Override
			public void onSureCallback(double radius) {
				MapDemo.this.radius = radius;

				OverlayOptions overlayOptions = null;

				if (FenceShape.circle == fenceShape) {
					overlayOptions = new CircleOptions().fillColor(0x000000FF).center(circleCenter)
							.stroke(new Stroke(5, Color.rgb(0xFF, 0x06, 0x01))).radius((int) radius);
				} else if (FenceShape.polygon == fenceShape) {
					overlayOptions = new PolygonOptions().points(mapVertexes)
							.stroke(new Stroke(mapVertexes.size(), Color.rgb(0xFF, 0x06, 0x01)))
							.fillColor(0x30FFFFFF);
				}

				// 把图画出来
				mBaiduMap.addOverlay(overlayOptions);
				isDraw = true;
				// 取消回调方法
				mBaiduMap.setOnMapClickListener(null);
			}

			@Override
			public void onCancelCallback() {
				resetStatus();
			}
		};

		/**
		 * 设置路线导航事件
		 */
		buttonSearch.setOnClickListener(v -> {
			if (!isOpen || mCurDogPosition == null) {
				ToastUtils.showToast("未获取到狗数据，请稍后重试");
				return;
			}
			if (mRouteSearch == null) {
				mRouteSearch = RoutePlanSearch.newInstance();
				mRouteSearch.setOnGetRoutePlanResultListener(MapDemo.this);
			}
			// 拿到此时的人的位置和狗的位置
			PlanNode startNode = PlanNode.withLocation(new LatLng(mCurrentLat, mCurrentLon));
			PlanNode endNode = PlanNode.withLocation(mCurDogPosition);
			mRouteSearch.walkingSearch((new WalkingRoutePlanOption())
					.from(startNode)
					.to(endNode));
		});
	}

	private void resetStatus() {
		isDraw = false;
		vertexIndex = 0;
		circleCenter = null;
		mapVertexes.clear();
		mBaiduMap.clear();
	}

	/**
	 * 这里实现回调
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (data.hasExtra("fenceShape")) {
			this.fenceShape = FenceShape.valueOf(data.getStringExtra("fenceShape"));
		}

		if (data.hasExtra("vertexesNumber")) {
			mVertexesNumber = data.getIntExtra("vertexesNumber",3);
		}

		// 这里是方法的回调
		mBaiduMap.setOnMapClickListener(this);
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
		// 先将狗的位置显示
		float curDogLon = dogInfo.getLongitude();
		float curDogLat = dogInfo.getLatitude();
		showDog(curDogLon, curDogLat);
		// 判断狗的位置，如果超出则报警
		if (!isInner(curDogLat, curDogLon)) {
			ToastUtils.showToast("狗过界了");
		}
		// TODO:判断狗的电量、状态信息
		checkDogStatus(dogInfo);
	}



	/**
	 * 判断狗的状态信息，并进行相应的处理
	 * @param dogInfo
	 */
	private void checkDogStatus(DogCurrentInfo dogInfo) {

	}

	/**
	 * 显示狗的位置
	 * @param longitude
	 * @param latitude
	 */
	private void showDog(double longitude, double latitude) {
		//定义Maker坐标点
		mCurDogPosition = new LatLng(latitude, longitude);
		//构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.mipmap.dog_64);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
				.position(mCurDogPosition)
				.icon(bitmap);
		//在地图上添加Marker，并显示
		if (curMarker != null) {
			curMarker.remove();
		}
		curMarker = (Marker) mBaiduMap.addOverlay(option);
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

	@Override
	public void onDataEmpty() {

	}

	/**
	 * 当画地图搞完了，只要点击地图就回调到这里。
	 * @param latLng
	 */
	@Override
	public void onMapClick(LatLng latLng) {
		switch (fenceShape) {
			case circle:
				circleCenter = latLng;
				break;

			case polygon:
				mapVertexes.add(latLng);//本地画图点集合
				vertexIndex++;
				BitmapUtil.getMark(mApplication, vertexIndex);
				OverlayOptions overlayOptions = new MarkerOptions().position(latLng)
						.icon(BitmapUtil.getMark(mApplication, vertexIndex)).zIndex(9).draggable(true);
				mBaiduMap.addOverlay(overlayOptions);
				break;
			default:
				break;
		}

		if (null == fenceCreateDialog) {
			fenceCreateDialog = new FenceCreateDialog(this, createCallback);
		}
		if (FenceShape.circle == fenceShape || vertexIndex == mVertexesNumber) {
			//定点数相同或者是画圆，就弹出框设置。
			fenceCreateDialog.setFenceShape(fenceShape);
			fenceCreateDialog.show();
		}
	}

	@Override
	public boolean onMapPoiClick(MapPoi mapPoi) {
		return false;
	}

	/**
	 * 步行路线的回调
	 * @param walkingRouteResult
	 */
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
		// 先将原来的路线移除
		mSearchOverlay.removeFromMap();
		// 添加新路线
		mSearchOverlay = new WalkingRouteOverlay(mBaiduMap);
		if (walkingRouteResult.getRouteLines().size() > 0) {
			//获取路径规划数据,(以返回的第一条数据为例)
			//为WalkingRouteOverlay实例设置路径数据
			mSearchOverlay.setData(walkingRouteResult.getRouteLines().get(0));
			//在地图上绘制WalkingRouteOverlay
			mSearchOverlay.addToMap();
		}
	}

	/**
	 * 定位SDK监听函数，在设定时间来一次（1s）
	 */
	public class MyLocationListener implements BDLocationListener {

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
					.direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
			mBaiduMap.setMyLocationData(locData);
//			showDog(location.getLongitude(), location.getLatitude());
			// 首次定位的时候，需要放大以观察位置。
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(18.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//				showDog(mCurDogPosition.longitude, mCurDogPosition.latitude);
			}

			/**
			 * 自己测试，判断是否超出边界
			 */
			if (isDraw) {
				if (isInner(mCurrentLat,mCurrentLon)) {
					ToastUtils.showToast("出界了");
				}
			}
		}

	}

	private boolean isInner(double mCurrentLat, double mCurrentLon) {
		LatLng curLatLng = new LatLng(mCurrentLat, mCurrentLon);
		boolean isInner = true;
		if (fenceShape == FenceShape.circle) {
			isInner = SpatialRelationUtil.isCircleContainsPoint(circleCenter, (int) radius, curLatLng);
		}
		if (fenceShape == FenceShape.polygon) {
			isInner = SpatialRelationUtil.isPolygonContainsPoint(mapVertexes, curLatLng);
		}
		return isInner;
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

	@Override
	public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

	}

	@Override
	public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

	}

	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

	}

	@Override
	public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

	}

}
