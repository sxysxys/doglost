//package com.shen.baidu.doglost.ui.activity;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.ResultReceiver;
//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SimpleItemAnimator;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bosy.robot.R;
//import com.bosy.robot.bean.CarData;
//import com.bosy.robot.bean.VideoInfo;
//import com.bosy.robot.bean.VideoVo;
//import com.bosy.robot.easyplayer.PlayFragment;
//import com.bosy.robot.http.ApiService;
//import com.bosy.robot.http.NetworkObserver;
//import com.bosy.robot.lib.component.SpTag;
//import com.bosy.robot.lib.util.StringUtil;
//import com.bosy.robot.lib.util.ToastUtils;
//import com.bosy.robot.oksocket.PulseBean;
//import com.bosy.robot.oksocket.SendData;
//import com.bosy.robot.ui.adapter.VideoListAdapter;
//import com.bosy.robot.ui.view.LaneLineView;
//import com.bosy.robot.ui.view.RobotPackViewNew;
//import com.bumptech.glide.Glide;
//import com.google.gson.reflect.TypeToken;
//import com.personal.framework.http.NetClient;
//import com.personal.framework.utils.GsonUtils;
//import com.personal.framework.utils.LogUtil;
//import com.personal.framework.utils.PreferenceUtils;
//import com.tencent.trtc.TRTCCloudDef;
//import com.tencent.trtc.TRTCCloudListener;
//import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
//import com.xuhao.didi.core.iocore.interfaces.ISendable;
//import com.xuhao.didi.core.pojo.OriginalData;
//import com.xuhao.didi.core.protocol.IReaderProtocol;
//import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
//import com.xuhao.didi.socket.client.sdk.OkSocket;
//import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
//import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
//import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
//import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
//import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;
//
//import java.nio.ByteOrder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//import timber.log.Timber;
//
//import static com.bosy.robot.http.UrlConstant.SOCKET_PORT;
//import static com.bosy.robot.http.UrlConstant.SOCKET_URL;
//
//
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//public class DisplayMainActivity extends BaseActivity implements Handler.Callback, View.OnClickListener{
//
//    private static String TAG = DisplayMainActivity.class.getSimpleName();
//
//
//    @BindView(R.id.reverse_car_track)
//    RobotPackViewNew mCarTrackView;
//
//
//
//
////    @BindView(R.id.tv_distance_1)
////    TextView mTVDistance1;
////    @BindView(R.id.tv_distance_2)
////    TextView mTVDistance2;
////    @BindView(R.id.tv_distance_3)
////    TextView mTVDistance3;
////    @BindView(R.id.tv_distance_4)
////    TextView mTVDistance4;
////    @BindView(R.id.tv_distance_5)
////    TextView mTVDistance5;
////    @BindView(R.id.tv_distance_6)
////    TextView mTVDistance6;
////    @BindView(R.id.tv_distance_7)
////    TextView mTVDistance7;
////    @BindView(R.id.tv_distance_8)
////    TextView mTVDistance8;
////    @BindView(R.id.tv_distance_9)
////    TextView mTVDistance9;
////    @BindView(R.id.tv_distance_10)
////    TextView mTVDistance10;
////    @BindView(R.id.tv_distance_11)
////    TextView mTVDistance11;
////    @BindView(R.id.tv_distance_12)
////    TextView mTVDistance12;
////    @BindView(R.id.tv_distance_13)
////    TextView mTVDistance13;
////    @BindView(R.id.tv_distance_14)
////    TextView mTVDistance14;
////    @BindView(R.id.tv_distance_15)
////    TextView mTVDistance15;
////    @BindView(R.id.tv_distance_16)
////    TextView mTVDistance16;
//    @BindView(R.id.ll_view)
//    LaneLineView mLaneLineView;
//
//    @BindView(R.id.img_up)
//    ImageView imgUp;
//
//    @BindView(R.id.img_run_state)
//    ImageView mImgRunState;
//
//    @BindView(R.id.recy_video_group_left)
//    RecyclerView mRecyclerViewLeft;
//
//    @BindView(R.id.recy_video_group_right)
//    RecyclerView mRecyclerViewRight;
//
//
//
//    public static final int MSG_SOCKET_CONNECT = 1888;
//
//    private final String[] PERMISSIONS = {
//            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//
//
//
//    private Handler mHandler;//主
//
//    private static final int MESSAGE_PLAY_MEDIA = 10001;
//    private static final int TEST_MESSAGE = 10010;
//    private static final int CHANGE_VIDEO_MESSAGE = 1100;//切换视频消息
//    private static final int CHANGE_VIDEO_URL_MESSAGE = 1101;//切换视频URL
//
//
//
//    private ConnectionInfo mInfo;
//    private OkSocketOptions mOkOptions;
//    private IConnectionManager mManager;
//    private ScheduledExecutorService executorService;
//    private int connectCount = 10;
//
//
//    ResultReceiver rr = new ResultReceiver(new Handler()){
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            super.onReceiveResult(resultCode, resultData);
//
//            if (resultCode == PlayFragment.RESULT_REND_START) {
//                Log.i("DisplayMainActivity","start进来了");
//                //onPlayStart();
//
//            } else if (resultCode == PlayFragment.RESULT_REND_STOP) {
//                Log.i("DisplayMainActivity","stop进来了");
//                //onPlayStop();
//            } else if (resultCode == PlayFragment.RESULT_REND_VIDEO_DISPLAY) {
//                Log.i("DisplayMainActivity","displayed进来了");
//                //onVideoDisplayed();
//
//            }
//
//        }
//
//
//    };
//
//
//
//    @Override
//    public void setLayoutId() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main);
//
//        //保持屏幕常亮 Keep the screen on
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        if (!hasPermissions(this, PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
//        }
//    }
//
//    @Override
//    public void initView() {
//
//        initTimber();
//        mHandler = new Handler(this);
//        initSocket();
//        //initVideoVo();
//
//    }
//
//    private void initVideoVo()
//    {
//        VideoVo videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0001");
//        videoVo.setUrl("rtmp://202.69.69.180:443/webcast/bshdlive-pc");
//        mVideoVosLeft.add(videoVo);
//
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0002");
//        videoVo.setUrl("rtmp://202.69.69.180:443/webcast/bshdlive-pc");
//        mVideoVosLeft.add(videoVo);
//
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0003");
//        videoVo.setUrl("rtmp://202.69.69.180:443/webcast/bshdlive-pc");
//        mVideoVosLeft.add(videoVo);
//
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0004");
//        videoVo.setUrl("rtmp://202.69.69.180:443/webcast/bshdlive-pc");
//        mVideoVosLeft.add(videoVo);
//
//
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0005");
//        videoVo.setUrl("rtmp://203.176.84.131:10085/hls/1");
//        mVideoVosRight.add(videoVo);
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0006");
//        videoVo.setUrl("rtmp://203.176.84.131:10085/hls/1");
//        mVideoVosRight.add(videoVo);
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0007");
//        videoVo.setUrl("rtmp://203.176.84.131:10085/hls/1");
//        mVideoVosRight.add(videoVo);
//        videoVo=new VideoVo();
//        videoVo.setName("安徽一附院机器人0008");
//        videoVo.setUrl("rtmp://203.176.84.131:10085/hls/1");
//        mVideoVosRight.add(videoVo);
//
//
//    }
//
//
//    @SuppressLint({"ClickableViewAccessibility", "HandlerLeak"})
//    @Override
//    public void initData(Intent intent) {
//
//
//        startPlay();
//
//        initPlayFragment();
//        //initRecyViewLeft();
//        //initRecyViewRight();
//
//    }
//
//    private void startPlay() {
//        new Thread() {
//            @Override
//            public void run() {
//                mHandler.sendEmptyMessageDelayed(TEST_MESSAGE,100);
//            }
//        }.start();
//    }
//    private VideoListAdapter mListAdapterLeft;
//    private VideoListAdapter mListAdapterRight;
//    private List<VideoVo> mVideoVosLeft=new ArrayList<>();
//    private List<VideoVo> mVideoVosRight=new ArrayList<>();
//
//    private void initRecyViewLeft()
//    {
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(DisplayMainActivity.this, 4);
//        gridLayoutManager.setOrientation(0);
//        mRecyclerViewLeft.setLayoutManager(gridLayoutManager);
//        mListAdapterLeft=new VideoListAdapter(DisplayMainActivity.this,(byte)1,R.layout.activity_video_item,mVideoVosLeft);
//        ((SimpleItemAnimator) mRecyclerViewLeft.getItemAnimator()).setSupportsChangeAnimations(false);
//        mRecyclerViewLeft.setAdapter(mListAdapterLeft);
//
//    }
//
//    private void initRecyViewRight()
//    {
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(DisplayMainActivity.this, 4);
//        gridLayoutManager.setOrientation(0);
//        mRecyclerViewRight.setLayoutManager(gridLayoutManager);
//        mListAdapterRight=new VideoListAdapter(DisplayMainActivity.this,(byte)2,R.layout.activity_video_item,mVideoVosRight);
//        ((SimpleItemAnimator) mRecyclerViewRight.getItemAnimator()).setSupportsChangeAnimations(false);
//        mRecyclerViewRight.setAdapter(mListAdapterRight);
//
//    }
//
//    private void startRealPlay() {
//        initVideoUrl();
//
//    }
//
//    private void initPlayFragment()
//    {
////        mRenderFragmentMain = PlayFragment.newInstance(DisplayMainActivity.this, "", rr);
////        mRenderFragmentMain.setScaleType(PlayFragment.FILL_WINDOW);
////        mRenderFragmentMain.toggleAudioEnable();
////        //f.setOnDoubleTapListener(this);
////
////        mRenderFragmentFront = PlayFragment.newInstance(DisplayMainActivity.this, "", rr);
////        mRenderFragmentFront.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
////        mRenderFragmentFront.toggleAudioEnable();
////
////        FragmentManager manager=getSupportFragmentManager();
////        manager.beginTransaction().add(R.id.realplay_sv_front,mRenderFragmentMain).add(R.id.player_ui_front,mRenderFragmentFront).commit();
//        PlayFragment mPlayFragment1 = PlayFragment.newInstance(this, "", rr);
//        mPlayFragment1.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//        mPlayFragment1.toggleAudioEnable();
//
//        PlayFragment mPlayFragment2 = PlayFragment.newInstance(this, "rtmp://203.176.84.131:10085/hls/1", rr);
//        mPlayFragment2.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//        mPlayFragment2.toggleAudioEnable();
//
//        PlayFragment mPlayFragment3 = PlayFragment.newInstance(this, "rtmp://203.176.84.131:10085/hls/1", rr);
//        mPlayFragment3.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//        mPlayFragment3.toggleAudioEnable();
//
//
//        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().add(R.id.realplay_sv_front,mPlayFragment1).add(R.id.player_ui_front,mPlayFragment2).add(R.id.player_ui_front_2,mPlayFragment3).commit();
//    }
//
//
//
//
//
//
//    private void initVideo(byte carId,String url1,String url2)
//    {
//        if(com.personal.framework.utils.StringUtil.isEmpty(url1) || com.personal.framework.utils.StringUtil.isEmpty(url2))
//        {
//            toast(getResources().getString(R.string.video_url_1),3);
//            return;
//        }
//
//        FragmentManager manager=getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        PlayFragment f = PlayFragment.newInstance(this, url1, rr);
//        f.setScaleType(PlayFragment.FILL_WINDOW);
//        //f.toggleAudioEnable();
//        //f.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//        //f.setOnDoubleTapListener(this);
//
//        transaction.add(R.id.realplay_sv_front,f,String.format("%d_1",carId));
//
//        f = PlayFragment.newInstance(this, url2, rr);
//        f.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//
//        transaction.add(R.id.player_ui_front,f,String.format("%d_2",carId)).commit();
//
////        if(com.personal.framework.utils.StringUtil.isEmpty(url2))
////        {
////            toast(getResources().getString(R.string.video_url_2),3);
////            //return;
////        }
//
//        //addVideoToHolder(url1, R.id.realplay_sv_front,1);
//
//        //addVideoToHolder(url2, R.id.player_ui_front,2);
//        //addVideoToHolder(carId,url1,url2,index);
//
//        changeVideoFragment(carId);
//    }
//
//
//    Map<Integer,List<VideoInfo>> videoInfos=new HashMap<>();
//
//    private void initVideoUrl()
//    {
//        if(PreferenceUtils.contains(DisplayMainActivity.this,SpTag.VIDEOURL))
//        {
//            Object o = PreferenceUtils.get(DisplayMainActivity.this, SpTag.VIDEOURL, "");
//            String str = String.valueOf(o);
//            if(!com.personal.framework.utils.StringUtil.isEmpty(str))
//            {
//                videoInfos= GsonUtils.fromJson(str,new TypeToken<Map<Integer,List<VideoInfo>>>() {}.getType());
//            }
//
//        }
//        else
//        {
//            Map<String, Object> map = new HashMap<>();
//            map.put(SpTag.APPID, appId);
//            NetClient.getRetrofit().create(ApiService.class)
//                    .getVideoList(map)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .compose(bindToLifecycle())
//                    .subscribe(new NetworkObserver<Object>(){
//                        @Override
//                        public void onSuccess(Object o) {
//                            //Timber.d(TAG,o.toString());
//                            String json = GsonUtils.toJson(o);
//                            if(!com.personal.framework.utils.StringUtil.isEmpty(json))
//                            {
//                                // TODO: 2020/04/13
//                                videoInfos= GsonUtils.fromJson(json,new TypeToken<Map<Integer,List<VideoInfo>>>() {}.getType());
//                                if(videoInfos!=null && videoInfos.size()>0)
//                                {
//                                    PreferenceUtils.put(DisplayMainActivity.this,SpTag.VIDEOURL,json);
//                                }
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Throwable e) {
//                            super.onFailure(e);
//                        }
//
//                    });
//        }
//
//
//    }
//
//
//    /**
//     * 用于监听TRTC事件
//     */
//    private TRTCCloudListener mChatRoomTRTCListener = new TRTCCloudListener() {
//        @Override
//        public void onEnterRoom(long result) {
//            if (result == 0) {
//                ToastUtils.success("进房成功",true);
//                //isOpenTalk=true;
//            }
//        }
//
//        @Override
//        public void onError(int errCode, String errMsg, Bundle extraInfo) {
//            ToastUtils.error("进房失败: " + errCode,true);
//            //isOpenTalk=false;
//        }
//
//        @Override
//        public void onRemoteUserEnterRoom(String userId) {
//
//        }
//
//        @Override
//        public void onRemoteUserLeaveRoom(String userId, int reason) {
//
//        }
//
//        @Override
//
//        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
//
//        }
//    };
//
//    private String userSign="";
//    private void initSign(String userId)
//    {
//        Map<String, Object> map = new HashMap<>();
//        map.put(SpTag.USERID, userId);
//        NetClient.getRetrofit().create(ApiService.class)
//                .getSign(map)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(bindToLifecycle())
//                .subscribe(new NetworkObserver<Object>(){
//                    @Override
//                    public void onSuccess(Object o) {
//                        //Timber.d(TAG,o.toString());
//                        String json = GsonUtils.toJson(o);
//                        if(!com.personal.framework.utils.StringUtil.isEmpty(json))
//                        {
//                            // TODO: 2020/04/13
//                            userSign=json;
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e) {
//                        super.onFailure(e);
//                    }
//
//                });
//    }
//
//    private void enterTRTCRoom() {
////        if(mTRTCCloud!=null)
////        {
////            if(com.personal.framework.utils.StringUtil.isEmpty(userSign))
////            {
////                toast("签名无效，初始化失败！",3);
////                return;
////            }
////            mTRTCCloud.enableAudioVolumeEvaluation(800);
////            mTRTCCloud.setListener(mChatRoomTRTCListener);
////            mTRTCCloud.startLocalAudio();
////            mTRTCCloud.setVoiceChangerType(TRTCCloudDef.TRTC_VOICE_CHANGER_TYPE_0);
////            // 拼接进房参数
////            TRTCCloudDef.TRTCParams params = new TRTCCloudDef.TRTCParams();
////            params.userSig= userSign;
////            params.roomId =Integer.parseInt(BuildConfig.RoomId) ;
////            params.sdkAppId=Integer.parseInt(BuildConfig.SdkAppId);
////            params.role = TRTCCloudDef.TRTCRoleAnchor;
////            params.userId = BuildConfig.UserId;
////            mTRTCCloud.enterRoom(params, TRTCCloudDef.TRTC_APP_SCENE_AUDIOCALL);
////        }
//
//    }
//
//
//
//
//
//
//
//
//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//    }
//
//
//    @SuppressLint("NewApi")
//    @Override
//    public boolean handleMessage(Message msg) {
//        if (this.isFinishing()) {
//            return false;
//        }
//        switch (msg.what) {
//            case TEST_MESSAGE:
//                startRealPlay();
//                break;
//            case MSG_SOCKET_CONNECT:
//                if (connectCount == 10) {
//                    if (mManager != null) {
//                        mManager.connect();
//                        connectCount = 0;
//                    }
//                }
//                connectCount++;
//                break;
//            case CHANGE_VIDEO_MESSAGE:
//                int carID=  msg.arg1;
//                if(carID!=0)
//                {
//                    carId=(byte) carID;
//                    if(mOnLineCarsMap!=null && mOnLineCarsMap.size()>0)
//                    {
//                        if(mOnLineCarsMap.containsKey( carId))
//                        {
//                            CarData carData = mOnLineCarsMap.get(carId);
//                            if(carData!=null)
//                            {
//                                updateMainCarState(carId,carData);
//                            }
//                        }
//                    }
//                    //changeVideoUrl((int) carId);
//                    changeVideoFragmentNew(carID);
//                    toast(getResources().getString(R.string.change_success),1);
//                }
//                /*
//                if(currentGear==0x20 && (cruiseState==1 || motionState==0))
//                {
//                    carId=(byte) carID;
//                    sendCarId(carId);
//                    setCarChooseColor(carId);
//                    if(mOnLineCarsMap!=null && mOnLineCarsMap.size()>0)
//                    {
//                        if(mOnLineCarsMap.containsKey(carId))
//                        {
//                            CarData carData = mOnLineCarsMap.get(carId);
//                            if(carData!=null)
//                            {
//                                updateMainCarState(carId,carData);
//                            }
//                        }
//                    }
//                    //changeVideoUrl((int) carId);
//                    changeVideoFragmentNew((int)carId);
//                    toast(getResources().getString(R.string.change_success),1);
//                }
//                else
//                {
//                    toast(getResources().getString(R.string.info_msg),3);
//                }
//                */
//                break;
//            case CHANGE_VIDEO_URL_MESSAGE:
//                Bundle bundle = msg.getData();
//                if(bundle!=null)
//                {
//
//                    changeVideoUrlNew(bundle.getString("url1"),bundle.getString("url2"));
//                }
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        exit();
//    }
//
//
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.test_btn:
//                exit();
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void changeVideoFragmentNew(Integer carId)
//    {
//        if(videoInfos!=null && videoInfos.size()>0) {
//            if (videoInfos.containsKey(carId)) {
//                List<VideoInfo> videoInfos = this.videoInfos.get(carId);
//                String url1 = "";
//                String url2 = "";
//                if (videoInfos != null && videoInfos.size() > 0) {
//                    for (VideoInfo videoInfo : videoInfos) {
//                        String videoName = videoInfo.getVideoName();
//                        if (!com.personal.framework.utils.StringUtil.isEmpty(videoName) && videoName.contains("_")) {
//                            String s = videoName.split("_")[0];
//                            if (!s.equals(String.valueOf(carId))) {
//                                continue;
//                            }
//                            s = videoName.split("_")[1];
//                            if (s.equals("1")) {
//                                url1 = videoInfo.getVideoUrl();
//                            } else if (s.equals("2")) {
//                                url2 = videoInfo.getVideoUrl();
//                            }
//                        }
//                    }
//                    //initVideo(carId, url1, url2);
//                    //initVideo(carId.byteValue(),url2, R.id.player_ui_front,2);
//
//
//                    Message message=mHandler.obtainMessage();
//                    message.what=CHANGE_VIDEO_URL_MESSAGE;
//                    Bundle bundle=new Bundle();
//                    bundle.putString("url1",url1);
//                    bundle.putString("url2",url2);
//                    message.setData(bundle);
//                    mHandler.sendMessage(message);
//
////                    String finalUrl = url1;
////                    String finalUrl1 = url2;
////                    Thread t = new Thread(new Runnable() {
////                        @Override
////                        public void run() {
////
////                            changeVideoUrlNew(finalUrl, finalUrl1);
////                        }
////                    });
////                    t.start();
//
//                }
//            } else {
//                toast(getResources().getString(R.string.systerm_error), 3);
//            }
//        }
//    }
//
//    private void replacePlayFragment(String url1, String url2) {
//        PlayFragment mPlayFragment1 = PlayFragment.newInstance(this, url1, rr);
//
//        mPlayFragment1.setScaleType(PlayFragment.FILL_WINDOW);
//
//        mPlayFragment1.toggleAudioEnable();
//
//        PlayFragment mPlayFragment2 = PlayFragment.newInstance(this, url2, rr);
//        mPlayFragment2.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//        mPlayFragment2.toggleAudioEnable();
//
//        PlayFragment mPlayFragment3 = PlayFragment.newInstance(this, url2, rr);
//        mPlayFragment3.setScaleType(PlayFragment.ASPECT_RATIO_CENTER_CROPS);
//        mPlayFragment3.toggleAudioEnable();
//
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.realplay_sv_front, mPlayFragment1)
//                .replace(R.id.player_ui_front, mPlayFragment2)
//                .replace(R.id.player_ui_front_2, mPlayFragment3)
//                .commit();
//    }
//
//    private void changeVideoUrlNew(String url1,String url2)
//    {
//        //getSupportFragmentManager().getFragments().clear();
//        //mRenderFragmentMain=null;
//        //mRenderFragmentFront=null;
//        //initPlayFragment();
//
//        if(com.personal.framework.utils.StringUtil.isEmpty(url1) || com.personal.framework.utils.StringUtil.isEmpty(url2))
//        {
//            toast(getResources().getString(R.string.video_url_1),3);
//            return;
//        }
//
//        replacePlayFragment(url1,url2);
//
//    }
//
//
//
//    private void changeVideoFragment(byte carId)
//    {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if(fragmentManager!=null)
//        {
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            List<Fragment> fragments = fragmentManager.getFragments();
//
//            if(fragments.size()>0)
//            {
//                for(int i=0,m=fragments.size();i<m;i++)
//                {
//
//                    Fragment tempfragment = fragments.get(i);
//                    if (tempfragment.getTag() != null && !tempfragment.getTag().contains("_")) {
//                        continue;
//                    }
//
//                    PlayFragment fragment = (PlayFragment)tempfragment;
//
//                    if(fragment!=null && fragment.isAudioEnable())
//                    {
//                        fragment.toggleAudioEnable();
//                    }
//                    if(fragment.getTag().equals(String.format("%d_1", carId)))
//                    {
//                        fragmentTransaction.show(fragment);
//                    }
//                    else if(fragment.getTag().equals(String.format("%d_2", carId)))
//                    {
//                        fragmentTransaction.show(fragment);
//                    }
//                    else
//                    {
//                        fragmentTransaction.hide(fragment);
//                    }
//                }
//                fragmentTransaction.commit();
//            }
//
//        }
//    }
//
//
//    private void sendCarId(byte carId)
//    {
//        if (mManager!=null && mManager.isConnect()) {
//            byte[] buffer = new byte[9];
//            buffer[0]=(byte) 0xFF;
//            buffer[1]=0x0F;
//
//            buffer[2]=0x04;
//
//            buffer[3]=appId;
//            buffer[4]=carId;
//            int crc = Crc16Sum(buffer, 5);
//            buffer[5] = (byte) (crc >> 8);
//            buffer[6] = (byte) crc;
//
//            buffer[7]=0x0D;
//            buffer[8]=0x0A;
//            mManager.send(new SendData(buffer));
//        }
//        else
//        {
//            toast("切换失败",3);
//        }
//    }
//
//    protected void toast(final String msg, final int type) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (type == 1) {
//                    ToastUtils.success(msg, false);
//                } else if (type == 2) {
//                    ToastUtils.warning(msg, false);
//                } else {
//                    ToastUtils.error(msg, false);
//                }
//            }
//        });
//    }
//
//
//    private void showPermissionsErrorAndRequest() {
//        Toast.makeText(this, "You need permissions before", Toast.LENGTH_SHORT).show();
//        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
//    }
//
//    private boolean hasPermissions(Context context, String... permissions) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
//            for (String permission : permissions) {
//                if (ActivityCompat.checkSelfPermission(context, permission)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//    private void initTimber() {
//        ThreadFactory namedThreadFactory = new ThreadFactory() {
//            @Override
//            public Thread newThread(@NonNull Runnable r) {
//                return new Thread(r, "thread_pool_updateHome");
//            }
//        };
//        /**
//         * 开定时器 2s发一次数据到服务器
//         */
//        executorService = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
//        executorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                // 需要做的事:发送消息
//                if (mManager != null) {
//                    //byte model=1;
//                    //byte[] buffer=new byte[]{0x00,0x00,0x00,0x12,0x00,0x00,0x00};
//                    if (mManager.isConnect()) {
//                        //byte[] buffer = getDirection(m_direction, m_strength);
//                        //mManager.send(new SendData((byte) 1, buffer));
//                    } else {
//                        Message message = new Message();
//                        message.what = MSG_SOCKET_CONNECT;
//                        mHandler.sendMessage(message);
//                    }
//                }
//            }
//        }, 1000, 200, TimeUnit.MILLISECONDS);
//    }
//
//    public static int Crc16Sum(byte[] buffer, int length) {
//        int crc = 0xFFFF;
//        byte i, j;
//        for (i = 0; i < length; i++) {
//            crc ^= (buffer[i] & 0xFF);
//            for (j = 0; j < 8; j++) {
//                if ((crc & 0x01) == 0x01)//
//                {
//                    crc >>= 1;
//                    crc ^= 0xA001;
//                } else {
//                    crc >>= 1;
//                }
//            }
//        }
//        return crc & 0xffff;
//    }
//
//
//
//    private void initSocket() {
//        mInfo = new ConnectionInfo(SOCKET_URL, SOCKET_PORT);
//        mOkOptions = new OkSocketOptions.Builder()
//                .setReconnectionManager(new NoneReconnect())
//                .setConnectTimeoutSecond(10)
//                .setPulseFrequency(5000)
//                .setPulseFeedLoseTimes(10)
//                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
//                    @Override
//                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
//                        mHandler.post(runnable);
//                    }
//                }).setReaderProtocol(new IReaderProtocol() {
//                    @Override
//                    public int getHeaderLength() {
//                        return 3;
//                    }
//
//                    @Override
//                    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
//                        return header[2];//代表一帧数据的有效长度
//                    }
//                })
//                .build();
//        mManager = OkSocket.open(mInfo).option(mOkOptions);
//        mManager.registerReceiver(adapter);
//        mManager.connect();
//    }
//
//    private byte preVoiceType=0;
//    private byte carId=0;
//    private byte appId = 0x12;
//    private byte currentGear=0;
//
//    private byte cruiseState=0;//巡航状态 0未开启 1开启
//
//    private byte motionState=0;//运动状态 0静止  1运动
//
//
//    private Map<Byte, CarData> mOnLineCarsMap=new HashMap<>();
//
//
//
//    private SocketActionAdapter adapter = new SocketActionAdapter() {
//        @Override
//        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
//            if (e != null) {
//                if (mManager != null) {
//                    //  mManager.connect();
//                }
//                Timber.d(e);
//                //ToastUtils.info("重新连接成功", true);
//            } else {
//                toast("正常断开", 2);
//            }
//        }
//
//        @Override
//        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
//            Timber.d("连接成功");
//            toast("连接成功", 1);
//            byte[] temp=new byte[]{(byte) 0xFF,0x0F,0x04,0x00,0x00,0x00,0x00, 0x0D, 0x0A};
//
//            temp[3]= appId;
//            int crc = Crc16Sum(temp, 5);
//            temp[5] = (byte) (crc >> 8);
//            temp[6] = (byte) crc;
//            mManager.getPulseManager().setPulseSendable(new PulseBean(temp)).pulse();
//        }
//
//        @Override
//        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
//            //ToastUtils.error("连接失败,请重试", true);
//            if (mManager != null) {
//                toast("连接失败,请重试", 3);
//                Timber.d("连接失败,请重试");
//                // mManager.connect();
//            }
//        }
//
//        @Override
//        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
//
//            if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0x0F) {//心跳喂狗
//                mManager.getPulseManager().feed();
//            }
//            else if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0x0A)//车体运行控制信息
//            {
//                byte[] buf = concat(data.getHeadBytes(), data.getBodyBytes());
//                if(buf.length!=14)
//                {
//                    return;
//                }
//
//                int crc16Sum = Crc16Sum(buf, buf.length - 2);
//                byte crcL = (byte) (crc16Sum & 0xFFFF);
//                byte crcH = (byte) ((crc16Sum & 0xFFFF) >> 8);
//
//                if (crcH == (byte) (buf[12] & 0xFF) && crcL ==(byte)(buf[13] & 0xFF)) {
//
//                    byte temp= (byte) (buf[3] & 0xFF);//车体编号
//
//                    CarData carData=null;
//
//                    if(mOnLineCarsMap.containsKey(temp))
//                    {
//                        carData = mOnLineCarsMap.get(temp);
//                    }
//                    else
//                    {
//                        carData=new CarData();
//                        carData.setCarId(temp);
////                        carData.setCarState((byte) 0);
////                        carData.setDirection((byte)0);
////                        carData.setFault((byte)0);
////                        carData.setLastCmd((byte)0);
////                        carData.setObstacle((byte) 0);
//                        mOnLineCarsMap.put(temp,carData);
//                    }
//
//                    if(carData!=null)
//                    {
//                        byte val= (byte) (buf[4] & 0xFF);//避障信息
//                        carData.setObstacle(val);
//
//                        //handleObstacle(val);
//
//                        val= (byte) (buf[5] & 0xFF);//(byte) (buf[6] & 0xFF)
//                        carData.setFault(val);
//
//                        //handleFault(val,val);//故障信息
//
//                        val= (byte) (buf[7] & 0xFF);//方向及变声
//                        //handleDirection(val);
//                        carData.setDirection(val);
//
//                        val= (byte) (buf[8] & 0xFF);//速度信息
//
//                        val= (byte) (buf[9] & 0xFF);//最后接受的命令
//
//                        val= (byte) (buf[10] & 0xFF);//车体状态信息
//                        //handleCarState(val);
//                        carData.setCarState(val);
//
//                        if(temp!=carId)
//                        {
//                            //updateOtherCarState(val,carData);
//                            return;
//                        }
//
//                        updateMainCarState(val,carData);
//
//                    }
//
//                    Log.d(TAG,StringUtil.bytes2HexString(buf));
//                }
//                else
//                {
//                    LogUtil.d(TAG, "控制信息校验和错误");
//                }
//
//
//            }
//            else if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0x0E)//车体切换命令
//            {
//                byte[] buf = concat(data.getHeadBytes(), data.getBodyBytes());
//                int length=buf.length;
//                if (buf.length != 8) {
//                    return;
//                }
//
//                int crc16Sum = Crc16Sum(buf, buf.length - 2);
//                byte crcL = (byte) (crc16Sum & 0xFFFF);
//                byte crcH = (byte) ((crc16Sum & 0xFFFF) >> 8);
//                if (crcH == (byte) (buf[length-2] & 0xFF) && crcL ==(byte)(buf[length-1] & 0xFF)) {
//
//                    byte val=(byte) (buf[3] & 0xFF);
//                    if(val!=appId)
//                    {
//
//                        return;
//                    }
//                    val=(byte) (buf[4] & 0xFF);//切换id
//                    carId=val;
//
//                    Message message=mHandler.obtainMessage();
//                    message.what=CHANGE_VIDEO_MESSAGE;
//                    message.arg1=val;
//                    mHandler.sendMessage(message);
//                    Log.d(TAG,StringUtil.bytes2HexString(buf));
//                }
//
//            }
//
//            else if(data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0X0B)//车体在线命令
//            {
//                byte[] buf = concat(data.getHeadBytes(), data.getBodyBytes());
//                int length=buf.length;
//                if(length!=8)
//                {
//                    return;
//                }
//
//                int crc16Sum = Crc16Sum(buf, length - 2);
//                byte crcL = (byte) (crc16Sum & 0xFFFF);
//                byte crcH = (byte) ((crc16Sum & 0xFFFF) >> 8);
//                if (crcH == (byte) (buf[length-2] & 0xFF) && crcL ==(byte)(buf[length-1] & 0xFF)) {
//                    byte val= (byte) (buf[3] & 0xFF);//车体编号
//                    if(val!=appId)
//                    {
//                        toast("app不匹配！",3);
//                        return;
//                    }
//                    // TODO: 2020/04/10
//                    val= (byte) (buf[4] & 0xFF);
//                    byte[] array = reserve(StringUtil.getBooleanArray(val));
//                    for (int i=0;i<8;i++)
//                    {
//                        int id=i+1;
//                        if(array[i]==1)
//                        {
//                            if(!mOnLineCarsMap.containsKey((byte)id))
//                            {
//                                CarData carData=new CarData();
//                                carData.setCarId((byte) id);
//                                mOnLineCarsMap.put((byte)id,carData);
//                            }
//                        }
//                        else
//                        {
//                            if(mOnLineCarsMap.containsKey((byte)id))
//                            {
//                                mOnLineCarsMap.remove((byte)id);
//                                if(carId==id)
//                                {
//                                    // TODO: 2020/04/11
////                                    if(mOnLineCarsMap.size()>0)
////                                    {
////                                        Byte aByte = getFirstOrNull(mOnLineCarsMap);
////                                        if(aByte!=0)
////                                        {
////                                            carId=aByte;
////                                            sendCarId(carId);
////                                            setCarChooseColor(carId);
////                                            changeVideoUrl(carId);
////                                        }
////                                    }
//                                    toast(getResources().getString(R.string.reselect),2);
//                                }
//                            }
//                        }
//                    }
//
//                    val= (byte) (buf[5] & 0xFF);
//                    array= reserve(StringUtil.getBooleanArray(val));
//
//                    for (int i=0;i<8;i++)
//                    {
//                        int id=i+9;
//                        if(array[i]==1)
//                        {
//                            if(!mOnLineCarsMap.containsKey((byte)id))
//                            {
//                                CarData carData=new CarData();
//                                carData.setCarId((byte) id);
//                                carData.setCarState((byte) 0);
//                                carData.setDirection((byte)0);
//                                carData.setFault((byte)0);
//                                carData.setLastCmd((byte)0);
//                                carData.setObstacle((byte) 0);
//
//                                mOnLineCarsMap.put((byte)id,carData);
//                            }
//                        }
//                        else
//                        {
//                            mOnLineCarsMap.remove((byte)id);
//                        }
//                    }
//
//
//                    if(carId==0)
//                    {
//                        if(mOnLineCarsMap.size()>0)
//                        {
//                            for(Byte by:mOnLineCarsMap.keySet())
//                            {
//                                carId=by;
//                                currentGear=0x20;
//                                Message message=mHandler.obtainMessage();
//                                message.what=CHANGE_VIDEO_MESSAGE;
//                                message.arg1=by;
//                                mHandler.sendMessage(message);
//                                //toast(getResources().getString(R.string.change_success),1);
//                                break;
//                            }
//                        }
//                    }
//
//                    Log.d(TAG,StringUtil.bytes2HexString(buf));
//                    //LogUtil.d(TAG,StringUtil.bytes2HexString(buf));
//                }
//
//            }
//
//        }
//
//
//        @Override
//        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
////            if (data instanceof SendData) {
////                SendData sendData = (SendData) data;
////                if (sendData != null) {
////
////                    LogUtil.d(TAG, bytes2HexString(sendData.parse()));
////                }
////            }
//
//
//
//        }
//
//
//        @Override
//        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
//            //            PulseBean bean=(PulseBean) data;
//            //            byte[] bytes =  bean.parse();
//            //            bytes = Arrays.copyOfRange(bytes, 0, bytes.length);
//            //            String str = bytes2HexString(bytes);
//            //            //com.personal.framework.utils.LogUtil.d(TAG, str);
//            //            System.out.println(str);
//        }
//    };
//
//    private void updateOtherCarState(byte val,CarData carData)
//    {
//        if(carData!=null)
//        {
//            byte fault = carData.getFault();
//            handleFault(fault,fault);
//
//        }
//    }
//
//    private void updateMainCarState(byte carId,CarData carData)
//    {
//        if(carData!=null)
//        {
//            byte val=0;
//            // TODO: 2020/04/11
//            if(carData.getObstacle()!=null)
//            {
//                val= carData.getObstacle();//避障信息
//                handleObstacle(val);
//            }
//
//            if(carData.getFault()!=null)
//            {
//                val= carData.getFault();//(byte) (buf[6] & 0xFF)
//
//                handleFault(val,val);//故障信息
//            }
//
//
//            if(carData.getDirection()!=null)
//            {
//                val= carData.getDirection();//方向及变声
//                handleDirection(val);
//            }
//
//
//            if(carData.getCarState()!=null)
//            {
//                val= carData.getCarState();//车体状态信息
//                //handleCarState(val);
//            }
//
//
//
//
//
//            //val= carData.getDirection();//速度信息
//
//            //val= carData.getDirection();//最后接受的命令
//
//
//        }
//
//    }
//
//    public static byte[] reserve( byte[] arr ){
//        byte[] arr1 = new byte[arr.length];
//        for( int x=0;x<arr.length;x++ ){
//            arr1[x] = arr[arr.length-x-1];
//        }
//        return arr1 ;
//    }
//    /**
//     * 处理避障信息
//     * @param val
//     */
//    private void handleObstacle(byte val)
//    {
//        //val=(byte) (0xFF & 0xFF);
//        boolean[] isDraw=new boolean[8];
//        byte bit=GetBit(val,0);//前1
//        if(bit==1)
//        {
//            isDraw[0]=true;
//        }
//        else
//        {
//            isDraw[0]=false;
//        }
//
//        bit=GetBit(val,1);//前2
//        if(bit==1)
//        {
//            isDraw[1]=true;
//        }
//        else
//        {
//            isDraw[1]=false;
//        }
//
//        bit=GetBit(val,2);//后1
//        if(bit==1)
//        {
//            isDraw[2]=true;
//        }
//        else
//        {
//            isDraw[2]=false;
//        }
//
//        bit=GetBit(val,3);//后2
//        if(bit==1)
//        {
//            isDraw[3]=true;
//        }
//        else
//        {
//            isDraw[3]=false;
//        }
//
//        bit=GetBit(val,4);//左1
//        if(bit==1)
//        {
//            isDraw[4]=true;
//        }
//        else
//        {
//            isDraw[4]=false;
//        }
//
//        bit=GetBit(val,5);//左2
//        if(bit==1)
//        {
//            isDraw[5]=true;
//        }
//        else
//        {
//            isDraw[5]=false;
//        }
//
//        bit=GetBit(val,6);//右1
//        if(bit==1)
//        {
//            isDraw[6]=true;
//        }
//        else
//        {
//            isDraw[6]=false;
//        }
//
//        bit=GetBit(val,7);//右2
//        if(bit==1)
//        {
//            isDraw[7]=true;
//        }
//        else
//        {
//            isDraw[7]=false;
//        }
//
//
//        if(mCarTrackView!=null)
//        {
//            mCarTrackView.setDistance(isDraw);
//        }
//    }
//
//
//    /**
//     * 处理故障信息
//     * @param val1
//     * @param val2
//     */
//    private void handleFault(byte val1,byte val2)
//    {
//
//    }
//
//    /**
//     * 处理方向信息及变声类型
//     * @param val
//     */
//    private void handleDirection(byte val)
//    {
//        byte bit=GetBit(val,7);//
//        if(bit==1)//运动
//        {
//            motionState=1;
//            if(mImgRunState!=null)
//            {
//                mImgRunState.setBackgroundResource(R.drawable.run_on);
//            }
//
//            bit=(byte) (val & 0x70);
//            if(this.imgUp.getVisibility()==View.INVISIBLE)
//            {
//                this.imgUp.setVisibility(View.VISIBLE);
//            }
//            showCarDirection(bit);
//        }
//        else//停止
//        {
//            motionState=0;
//            if(mImgRunState!=null)
//            {
//                mImgRunState.setBackgroundResource(R.drawable.run_off);
//            }
//            //showDefaultDirection();
//            if(this.imgUp.getVisibility()==View.INVISIBLE)
//            {
//                this.imgUp.setVisibility(View.VISIBLE);
//            }
//            Glide
//                    .with(DisplayMainActivity.this)
//                    .asGif()
//                    .load(R.drawable.img_park)
//                    .into(imgUp);
//
//        }
//
//
//
//
//    }
//
//
//
//    private byte GetBit(byte b,int index)
//    {
//        return (byte)(((b & (1 << index)) >0)?1:0);
//    }
//
//    public static <T> byte[] concat(byte[] first, byte[] second) {
//        byte[] result = Arrays.copyOf(first, first.length + second.length);
//        System.arraycopy(second, 0, result, first.length, second.length);
//        return result;
//    }
//
//    private void showCarDirection(byte val)
//    {
//        /*
//        int index= val / 16;
//        for(int i=0;i<8;i++)
//        {
//            if(i==index)
//            {
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(drawableVal[i])
//                        .into(mImageViews[i]);
//            }
//            else
//            {
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asBitmap()
//                        .load(drawableVal[i])
//                        .into(mImageViews[i]);
//            }
//        }
//        */
//        switch (val)
//        {
//            case 0:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.up)
//                        .into(imgUp);
//                break;
//            case 16:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.down)
//                        .into(imgUp);
//                break;
//            case 32:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.pull_left)
//                        .into(imgUp);
//                break;
//            case 48:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.pull_right)
//                        .into(imgUp);
//                break;
//            case 64:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.turn_left)
//                        .into(imgUp);
//                break;
//            case 80:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.turn_right)
//                        .into(imgUp);
//                break;
//            case 96:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.roation_left)
//                        .into(imgUp);
//                break;
//            case 112:
//                Glide
//                        .with(DisplayMainActivity.this)
//                        .asGif()
//                        .load(R.drawable.roation_right)
//                        .into(imgUp);
//                break;
//                default:
//                    Glide
//                            .with(DisplayMainActivity.this)
//                            .asGif()
//                            .load(R.drawable.img_park)
//                            .into(imgUp);
//                    break;
//        }
//    }
//
////    private void showDefaultDirection()
////    {
////        for(int i=0;i<8;i++)
////        {
////            Glide
////                    .with(DisplayMainActivity.this)
////                    .asBitmap()
////                    .load(drawableVal[i])
////                    .into(mImageViews[i]);
////        }
////    }
//
//    private void exit() {
//
//        if(mVideoVosLeft!=null && mVideoVosLeft.size()>0)
//        {
//            mVideoVosLeft.clear();
//            mListAdapterLeft.notifyDataSetChanged();
//        }
//
//        if(mVideoVosRight!=null && mVideoVosRight.size()>0)
//        {
//            mVideoVosRight.clear();
//            mListAdapterRight.notifyDataSetChanged();
//        }
//        cancelTimer();
//        if (mManager != null) {
//           //sendCarId((byte) 0);
//            mManager.disconnect();
//            mManager.unRegisterReceiver(adapter);
//        }
//
//        if (mHandler != null) {
//            mHandler.removeMessages(MSG_SOCKET_CONNECT);
//            mHandler.removeMessages(TEST_MESSAGE);
//            mHandler.removeMessages(CHANGE_VIDEO_MESSAGE);
//            mHandler = null;
//        }
//
//        finish();
//    }
//
//    @Override
//    public void finish() {
//
//        super.finish();
//    }
//
//    private void cancelTimer() {
//        if (executorService != null) {
//            try {
//                executorService.shutdown();
//                Timber.d("shutdown");
//                if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
//                    executorService.shutdownNow();
//                    Timber.d("shutdownNow");
//                }
//            } catch (InterruptedException e) {
//                Timber.d(e.toString());
//                executorService.shutdownNow();
//            }
//            executorService = null;
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
//    }
//
//    /**
//     * 获取map中第一个数据值
//     *
//     * @param <K> Key的类型
//     * @param <V> Value的类型
//     * @param map 数据源
//     * @return 返回的值
//     */
//    public static <K, V> K getFirstOrNull(Map<K, V> map) {
//        K obj = null;
//        for (Map.Entry<K, V> entry : map.entrySet()) {
//            obj = entry.getKey();
//            if (obj != null) {
//                break;
//            }
//        }
//        return obj;
//    }
//
//}
