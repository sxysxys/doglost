package com.shen.baidu.doglost.presenter.impl;

import com.shen.baidu.doglost.model.Api;
import com.shen.baidu.doglost.model.domain.ResponseLogin;
import com.shen.baidu.doglost.presenter.ILoginPresenter;
import com.shen.baidu.doglost.utils.RetrofitManager;
import com.shen.baidu.doglost.utils.ToastUtils;
import com.shen.baidu.doglost.view.ILoginInfoCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginPresenterImpl implements ILoginPresenter {

    private static ILoginPresenter loginPresenter;

    private LoginPresenterImpl(){}

    public static ILoginPresenter getInstance() {
        if (loginPresenter == null) {
            loginPresenter = new LoginPresenterImpl();
        }
        return loginPresenter;
    }

    ILoginInfoCallback loginInfoCallback;

    @Override
    public void loginRequest(String loginName, String loginPassword) {
        if (loginInfoCallback != null) {
            loginInfoCallback.loading();
        }
        Call<ResponseLogin> task = getCall(loginName, loginPassword);
        task.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                ResponseLogin body = response.body();
                if (response.code() == HTTP_OK && body != null) {
                    handleResult(body);
                } else {
                    if (loginInfoCallback != null) {
                        loginInfoCallback.onNetError();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                t.printStackTrace();
                if (loginInfoCallback != null) {
                    loginInfoCallback.onNetError();
                }
            }
        });
    }

    private void handleResult(ResponseLogin body) {
        if (loginInfoCallback != null) {
            loginInfoCallback.onNetDataLoaded(body);
        }
    }

    private Call<ResponseLogin> getCall(String loginName, String loginPassword) {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        return api.login(loginName, loginPassword);
    }

    @Override
    public void registerCallback(ILoginInfoCallback callBack) {
        this.loginInfoCallback = callBack;
    }

    @Override
    public void unregisterCallback(ILoginInfoCallback callback) {
        if (loginInfoCallback == callback) {
            this.loginInfoCallback = null;
        }
    }

}
