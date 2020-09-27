package com.shen.baidu.doglost.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.constant.Const;
import com.shen.baidu.doglost.model.domain.ResponseLogin;
import com.shen.baidu.doglost.presenter.ILoginPresenter;
import com.shen.baidu.doglost.presenter.impl.LoginPresenterImpl;
import com.shen.baidu.doglost.utils.LogUtils;
import com.shen.baidu.doglost.utils.PassWordUtil;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.ILoginInfoCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@RequiresApi(api = Build.VERSION_CODES.M)
public class LoginActivity extends AppCompatActivity implements ILoginInfoCallback {

    @BindView(R.id.btn_login)
    Button loginButton;

    @BindView(R.id.login_name)
    EditText loginName;

    @BindView(R.id.login_password)
    EditText loginPassWord;

    private LoginActivity.SDKReceiver mReceiver;

    private ILoginPresenter mLoginPresenter;
    private SharedPreferences mSharedPreferences;
    private static final int PERMISSION_RESULT_CODE = 1;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(LoginActivity.this,"apikey验证失败，地图功能无法正常使用",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(LoginActivity.this,"apikey验证成功",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(LoginActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 申请权限
        int sdkInt = Build.VERSION.SDK_INT;
        // 拿到的是机子本地的sdk版本
        if (sdkInt > Build.VERSION_CODES.M) {
            checkPermission();
        }
        initView();
        // api key的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；api key授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        initReceiver();
        initListener();
        initPresenter();
    }

    /**
     * 验证权限
     */
    private void checkPermission() {
        // 请求定位权限
        int gps = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int wifi = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int offLine = checkSelfPermission(READ_PHONE_STATE);
        if (gps != PERMISSION_GRANTED || wifi != PERMISSION_GRANTED ||offLine != PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE},PERMISSION_RESULT_CODE);
        }
    }

    /**
     * 请求权限的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RESULT_CODE && grantResults.length == 3 && grantResults[0] == PERMISSION_GRANTED
                && grantResults[1] == PERMISSION_GRANTED && grantResults[2] == PERMISSION_GRANTED) {
            LogUtils.d(this,"开启了定位和通话权限");
        } else {
            finish();
        }

    }

    private void initReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new LoginActivity.SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    private void initPresenter() {
        mLoginPresenter = LoginPresenterImpl.getInstance();
        mLoginPresenter.registerCallback(this);
    }

    private void initListener() {
        loginButton.setOnClickListener(v -> {
            // 先判断有没有输入
            String loginText = loginName.getText().toString();
            String loginPassWord = this.loginPassWord.getText().toString();
            if (loginText == "" || loginPassWord == "") {
                ToastUtils.showToast("请输入登录信息!");
                return;
            }
//            skipMain();
            // 发起请求
            mLoginPresenter.loginRequest(loginText, loginPassWord);
        });
    }



    private void initView() {
        ButterKnife.bind(this);
    }

    /**
     * 数据回调
     * @param data
     */
    @Override
    public void onNetDataLoaded(ResponseLogin data) {
        if (data.getCode() == 200) {
            // 此时说明已经验证通过，将用户的用户名和密码保存起来。
            PassWordUtil.getInstance().save(loginName.getText().toString(), loginPassWord.getText().toString());
            // 此时确定账号密码了
            Const.deviceId = Integer.parseInt(loginName.getText().toString().substring(3,6));
            // 此时执行跳转
            skipMain();
        } else if (data.getCode() == 400){
            ToastUtils.showToast("用户名或密码不正确");
        }
    }


    /**
     * 执行跳转
     */
    private void skipMain() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        ToastUtils.showToast("登录成功");
    }

    @Override
    public void onDataEmpty() {

    }

    /**
     * 网络错误
     */
    @Override
    public void onNetError() {
        ToastUtils.showToast("网络错误，请检查网络");
    }

    @Override
    public void loading() {
        LogUtils.d(this, "正在请求登录");
    }

    @Override
    public void onNetSuccess() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
