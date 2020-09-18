package com.shen.baidu.doglost.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

    @BindView(R.id.layout_test)
    public Button testLayout;

    @BindView(R.id.history)
    public Button hisButton;

    private Unbinder mBind;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = ButterKnife.bind(this);
        initListener();
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

        testLayout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestLayoutActivity.class);
            MainActivity.this.startActivity(intent);
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
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
