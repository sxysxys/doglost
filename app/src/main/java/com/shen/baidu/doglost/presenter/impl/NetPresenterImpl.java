package com.shen.baidu.doglost.presenter.impl;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.shen.baidu.doglost.model.domain.DogCurrentInfo;
import com.shen.baidu.doglost.model.domain.SendBean;
import com.shen.baidu.doglost.constant.INetParams;
import com.shen.baidu.doglost.presenter.INetPresenter;
import com.shen.baidu.doglost.utils.DataHandlerUtil;
import com.shen.baidu.doglost.utils.LogUtils;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.INetCallBack;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;

import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.shen.baidu.doglost.utils.DataHandlerUtil.Crc16Sum;

/**
 * 服务器数据提供，设置成单例模式
 */
public class NetPresenterImpl implements INetPresenter {

    private static final int MSG_SOCKET_CONNECT = 1888;
    private int connectCount = 10;

    private static INetPresenter presenter;
    private ConnectionInfo mInfo;
    private OkSocketOptions mOkOptions;
    private IConnectionManager mManager;

    INetCallBack netCallBack;
    private ScheduledThreadPoolExecutor executorService;
    private Handler mHandler;

    private boolean isNeedStart = true;

//    /**
//     * 发送的那个标志位
//     */
//    private byte mFlag = 0b00000000;

    /**
     * 成功连上了，才会有这个。
     */
    private SendBean mSendData;

    @Override
    public byte getmFlag() {
        if (mSendData != null && mManager.isConnect()) {
            return mSendData.getMFlag();
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void setmFlag(byte mFlag) {
        if (mSendData != null && mManager.isConnect()) {
            mSendData.setMFlag(mFlag);
        }
    }

    private NetPresenterImpl() {}

    public static INetPresenter getInstance() {
        if (presenter == null) {
            presenter = new NetPresenterImpl();
        }
        return presenter;
    }

    @Override
    public void firstConnect() {
        initHandler();
        initManager();
        connect();
    }

    @Override
    public void connect() {
        doConnect();
        initTimber();
    }

    /**
     * 开启定时任务，每
     */
    private void initHandler() {
        mHandler = new Handler(msg -> {
            // 断线重连
            if (!isNeedStart) {
                return false;
            }
            switch (msg.what) {
                case MSG_SOCKET_CONNECT:
                    if (connectCount == 10) {
                        if (mManager != null && !mManager.isConnect()) {
                            mManager.connect();
                        }
                        connectCount = 0;
                    }
                    connectCount ++;
                    break;
                default:
                    break;
            }
            return false;
        });
    }


    @Override
    public void delConnect() {
        isNeedStart = false;
        executorService.shutdown();
        if (mManager != null && mManager.isConnect()) {
            mManager.disconnect();
        }
    }

    /**
     * 发送数据，只需要send就行了。
     * @param dataBean
     */
    @Override
    public void sendData(SendBean dataBean) {
        if (mManager != null) {
            if (mManager.isConnect()) {
                mManager.send(dataBean);
            } else {
                ToastUtils.showToast("请先开启小狗定位!");
            }
        }
    }

    @Override
    public void registerCallback(INetCallBack callBack) {
        this.netCallBack = callBack;
    }

    @Override
    public void unregisterCallback(INetCallBack callback) {
        if (netCallBack != null) {
            this.netCallBack = null;
        }
    }

    private void doConnect() {
        isNeedStart = true;
        if (mManager != null) {
            mManager.connect();
        }
    }

    /**
     * 初始化连接参数
     */
    private void initManager() {
        mInfo = new ConnectionInfo(INetParams.ip, INetParams.port);
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setPulseFrequency(5000)
                .setPulseFeedLoseTimes(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        mHandler.post(runnable);
                    }
                })
                // 这里配置reader的接收字节，每次接收数据的时候都会进入这里，用来封装OriginalData，然后进入onSocketReadResponse。
                // 如果没有接收完，还会进到这里。TODO
                .setReaderProtocol(new IReaderProtocol() {
                    /**
                     * 每帧数据头的长度，如果这俩个方法执行完数据还没读完的话，就会再次执行这两个方法。
                     * @return
                     */
                    @Override
                    public int getHeaderLength() {
                        return 3;
                    }

                    @Override
                    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                        return header[2];
                    }
                })
                .build();
        // 具体的连接信息。
        IConnectionManager open = OkSocket.open(mInfo);
        // 配置option，也就是对通道的配置。
        mManager = open.option(mOkOptions);
        mManager.registerReceiver(adapter);
    }

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            /**
             * 开启心跳
             */
            mSendData = SendBean.getInstance();
            mManager.getPulseManager().setPulseSendable(mSendData).pulse();
            if (netCallBack != null) {
                netCallBack.onNetSuccess();
            }
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            // 异常断开，可能没有及时喂狗等。
            if (e != null) {
                LogUtils.w(NetPresenterImpl.this, "异常断开");
                if (netCallBack != null) {
                    netCallBack.onNetError();
                }
            }
            // 正常断开，使用disconnect来断开。
            else {
                LogUtils.d(NetPresenterImpl.this, "正常断开");
                if (netCallBack != null) {
                    netCallBack.onConnectQuit();
                }
            }
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            LogUtils.w(NetPresenterImpl.this, "连接失败");
            if (netCallBack != null) {
                netCallBack.onNetError();
            }
        }

        /**
         * 有东西回来，就在这里回调，封装形式是默认前4位是int长度，后面的是真正的body数据，可以自行配置。
         * TODO
         * @param info
         * @param data   原始的读取到的数据{@link OriginalData}
         */
        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            /**
             * 如果是服务器回的是喂狗信息，那么就喂狗
             */
            byte[] headBytes = data.getHeadBytes();
            if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0xE2) {
                mManager.getPulseManager().feed();
            }
            /**
             * 定位数据正常回送，将数据封装，回调。
             */
            if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0xE1) {
                // TODO 进行CRC校验
                byte[] buf = concat(data.getHeadBytes(), data.getBodyBytes());
                int crc16Sum = Crc16Sum(buf, buf.length - 2);
                byte crcL = (byte) (crc16Sum & 0xFFFF);
                byte crcH = (byte) ((crc16Sum & 0xFFFF) >> 8);
                if (crcH == (byte) (buf[15] & 0xFF) && crcL ==(byte)(buf[16] & 0xFF)) {
                    // TODO 将数据封装
                    // 经度
//                    byte[] lon = new byte[]{buf[7],buf[6],buf[5],buf[4]};
//                    byte[] lat = new byte[]{buf[11],buf[10],buf[9],buf[8]};

                    byte[] lon = new byte[]{buf[4],buf[5],buf[6],buf[7]};
                    byte[] lat = new byte[]{buf[8],buf[9],buf[10],buf[11]};
                    double lonfloat = DataHandlerUtil.getFloat2(lon);
                    double latfloat = DataHandlerUtil.getFloat2(lat);
                    // 纬度
                    DogCurrentInfo dogCurrentInfo = new DogCurrentInfo.Builder().
                            lon(lonfloat).lat(latfloat)
                            .bat(buf[12]).status(buf[13]).build();
                    netCallBack.onNetDataLoaded(dogCurrentInfo);
                }
            }
        }

        /**
         * 发送的时候进入这里
         * TODO
         * @param info
         * @param data   写出的数据{@link ISendable}
         */
        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
//            String str = new String(data.parse(), Charset.forName("utf-8"));
//            logSend(str);
        }

        /**
         * 每次在心跳的时候发送。
         * @param info
         * @param data
         */
        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
//            String str = new String(data.parse(), Charset.forName("utf-8"));
//            logSend(str);
        }
    };

//    /**
//     * 发送心跳数据
//     * @return
//     */
//    private byte[] getTempBytes() {
//        byte[] temp=new byte[]{(byte) 0xFF,(byte) 0xE2,0x05,0x00,0x00,0x00,0x00, 0x00, 0x0D, 0x0A};
//        temp[3]= 0x01;
//        int crc = Crc16Sum(temp, 6);
//        temp[6] = (byte) (crc >> 8);
//        temp[7] = (byte) crc;
//        return temp;
//    }

    public static <T> byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * 初始化定时器
     */
    private void initTimber() {
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "thread_pool_updateHome");
            }
        };
        /**
         * 开定时器 0.2s发一次数据到服务器
         */
        executorService = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
        executorService.scheduleAtFixedRate(() -> {
            if (mManager != null) {
                // 如果此时已经连上了，就一直给服务器发心跳包。
                if (mManager.isConnect()) {
//                    mManager.send(new PulseBean());
                }
                // 如果没有连上，就进入断线重连。
                else {
                    Message message = new Message();
                    message.what = MSG_SOCKET_CONNECT;
                    mHandler.sendMessage(message);
                }
            }
        }, 1000, 200, TimeUnit.MILLISECONDS);
    }
}
