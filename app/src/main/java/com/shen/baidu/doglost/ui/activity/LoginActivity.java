package com.shen.baidu.doglost.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.model.domain.ResponseLogin;
import com.shen.baidu.doglost.presenter.ILoginPresenter;
import com.shen.baidu.doglost.presenter.impl.LoginPresenterImpl;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.ILoginInfoCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements ILoginInfoCallback {

    @BindView(R.id.btn_login)
    Button loginButton;

    @BindView(R.id.login_name)
    EditText loginName;

    @BindView(R.id.login_password)
    EditText loginPassWord;

    private LoginActivity.SDKReceiver mReceiver;

    private ILoginPresenter mLoginPresenter;

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
        initView();
        // apikey的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；apikey授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        initReceiver();
        initListener();
        initPresenter();
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
            skipMain();
            // 发起请求
//            mLoginPresenter.loginRequest(loginText, loginPassWord);
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
        Intent intent = new Intent(this, MainActivity.class);
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
