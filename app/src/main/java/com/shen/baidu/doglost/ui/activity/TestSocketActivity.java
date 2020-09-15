package com.shen.baidu.doglost.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.adapter.LogAdapter;
import com.shen.baidu.doglost.model.domain.LogBean;
import com.shen.baidu.doglost.model.domain.MsgDataBean;
import com.shen.baidu.doglost.model.domain.PulseBean;
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
import java.nio.charset.Charset;

import static android.widget.Toast.LENGTH_SHORT;
import static com.shen.baidu.doglost.utils.DataHandlerUtil.Crc16Sum;

public class TestSocketActivity extends AppCompatActivity {

    private ConnectionInfo mInfo;

    private Button mConnect;

    private EditText mIPET;
    private EditText mPortET;
    private IConnectionManager mManager;
    private EditText mSendET;
    private OkSocketOptions mOkOptions;
    private Button mClearLog;
    private Button mSendBtn;

    private RecyclerView mSendList;
    private RecyclerView mReceList;

    private LogAdapter mSendLogAdapter = new LogAdapter();
    private LogAdapter mReceLogAdapter = new LogAdapter();

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
//            mManager.send(new HandShakeBean());
            mConnect.setText("DisConnect");
            mIPET.setEnabled(false);
            mPortET.setEnabled(false);
            /**
             * 开始发送心跳连接。
             */

            byte[] temp=new byte[]{(byte) 0xFF,(byte) 0xE2,0x05,0x00,0x00,0x00,0x00, 0x00, 0x0D, 0x0A};

            temp[3]= 0x01;
            int crc = Crc16Sum(temp, 6);
            temp[6] = (byte) (crc >> 8);
            temp[7] = (byte) crc;

            mManager.getPulseManager().setPulseSendable(new PulseBean(temp)).pulse();
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                logSend("异常断开(Disconnected with exception):" + e.getMessage());
            } else {
                logSend("正常断开(Disconnect Manually)");
            }
            mConnect.setText("Connect");
            mIPET.setEnabled(true);
            mPortET.setEnabled(true);
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            logSend("连接失败(Connecting Failed)");
            mConnect.setText("Connect");
            mIPET.setEnabled(true);
            mPortET.setEnabled(true);
        }

        /**
         * 有东西回来，就在这里回调，封装形式是默认前4位是int长度，后面的是真正的body数据，可以自行配置。
         * TODO
         * @param info
         * @param action {@link IAction#ACTION_READ_COMPLETE}
         * @param data   原始的读取到的数据{@link OriginalData}
         */
        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            /**
             * 喂狗
             */
            byte[] headBytes = data.getHeadBytes();
            if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0xE2) {
                mManager.getPulseManager().feed();
            }
            /**
             * 数据正常回送。
             */
            if (data.getHeadBytes()[0] == (byte) 0xFF && data.getHeadBytes()[1] == (byte) 0xE1) {
                String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
                logRece(str);
            }
        }

        /**
         * 发送的时候进入这里
         * TODO
         * @param info
         * @param action {@link IAction#ACTION_WRITE_COMPLETE}
         * @param data   写出的数据{@link ISendable}
         */
        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }

        /**
         * 每次在心跳的时候发送。
         * @param info
         * @param data
         */
        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);
        findViews();
        initData();
        setListener();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void findViews() {
        mSendList = findViewById(R.id.send_list);
        mReceList = findViewById(R.id.rece_list);
        mIPET = findViewById(R.id.ip);
        mPortET = findViewById(R.id.port);
        mClearLog = findViewById(R.id.clear_log);
        mConnect = findViewById(R.id.connect);
        mSendET = findViewById(R.id.send_et);
        mSendBtn = findViewById(R.id.send_btn);
    }

    private void initData() {
        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSendList.setLayoutManager(manager1);
        mSendList.setAdapter(mSendLogAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReceList.setLayoutManager(manager2);
        mReceList.setAdapter(mReceLogAdapter);

        initManager();
    }

    /**
     * 设置此次连接的信息。
     */
    private void initManager() {
        final Handler handler = new Handler();
        mIPET.setText("60.166.106.91");
        mPortET.setText("8233");
        mInfo = new ConnectionInfo(mIPET.getText().toString(), Integer.parseInt(mPortET.getText().toString()));
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setPulseFrequency(5000)
                .setPulseFeedLoseTimes(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
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


    private void setListener() {
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    initManager();
                    mManager.connect();
                    mIPET.setEnabled(false);
                    mPortET.setEnabled(false);
                } else {
                    mConnect.setText("Disconnecting");
                    mManager.disconnect();
                }
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    String msg = mSendET.getText().toString();
                    if (TextUtils.isEmpty(msg.trim())) {
                        return;
                    }
                    // 这里将发送的string进行一个封装，按照指定的格式发出。
                    MsgDataBean msgDataBean = new MsgDataBean(msg);
                    mManager.send(msgDataBean);
                    mSendET.setText("");
                }
            }
        });
        mClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceLogAdapter.getDataList().clear();
                mSendLogAdapter.getDataList().clear();
                mReceLogAdapter.notifyDataSetChanged();
                mSendLogAdapter.notifyDataSetChanged();
            }
        });
    }

    private void logSend(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            mSendLogAdapter.getDataList().add(0, logBean);
            mSendLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    logSend(threadName + " 线程打印(In Thread):" + log);
                }
            });
        }
    }

    private void logRece(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            mReceLogAdapter.getDataList().add(0, logBean);
            mReceLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    logRece(threadName + " 线程打印(In Thread):" + log);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }

    /**
     * 发送数据
     * @param carId
     */
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
//    }
}