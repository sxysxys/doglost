package com.shen.baidu.doglost.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.shen.baidu.doglost.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.test_socket)
    public Button socketButton;

    @BindView(R.id.sdk)
    public Button sdk;

    @BindView(R.id.map)
    public Button map;

    @BindView(R.id.sta)
    public Button sta;

    @BindView(R.id.dyn)
    public Button dyn;

    @BindView(R.id.history)
    public Button hisButton;

    private SDKReceiver mReceiver;
    private Unbinder mBind;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(MainActivity.this,"apikey验证失败，地图功能无法正常使用",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(MainActivity.this,"apikey验证成功",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = ButterKnife.bind(this);

        // apikey的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；apikey授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        initReceiver();

        initListener();

    }

    private void initReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    private void initListener() {
        sdk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SdkDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        sta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaticDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        dyn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DynamicDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        socketButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestSocketActivity.class);
            MainActivity.this.startActivity(intent);
        });

        hisButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            MainActivity.this.startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
