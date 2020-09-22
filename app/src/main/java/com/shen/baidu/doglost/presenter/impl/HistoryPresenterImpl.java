package com.shen.baidu.doglost.presenter.impl;

import com.shen.baidu.doglost.constant.Const;
import com.shen.baidu.doglost.model.Api;
import com.shen.baidu.doglost.model.domain.HistoryPoint;
import com.shen.baidu.doglost.presenter.IHistoryPresenter;
import com.shen.baidu.doglost.utils.RetrofitManager;
import com.shen.baidu.doglost.view.IHistoryCallback;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 历史轨迹提供，单例模式
 */
public class HistoryPresenterImpl implements IHistoryPresenter {

    IHistoryCallback callback;

    private static IHistoryPresenter historyPresenter = null;

    private HistoryPresenterImpl(){}

    public static IHistoryPresenter getInstance() {
        if (historyPresenter == null) {
            historyPresenter = new HistoryPresenterImpl();
        }
        return historyPresenter;
    }

    @Override
    public void getHisPoint(int deviceId, String startTime, String endTime) {
        callback.loading();
        Call<HistoryPoint> call = getCall(startTime, endTime);
        call.enqueue(new Callback<HistoryPoint>() {
            @Override
            public void onResponse(Call<HistoryPoint> call, Response<HistoryPoint> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HistoryPoint historyPoints = response.body();
                    handleResult(historyPoints);
                } else {
                    handleError();
                }
            }

            @Override
            public void onFailure(Call<HistoryPoint> call, Throwable t) {
                t.printStackTrace();
                handleError();
            }
        });
    }

    /**
     * 此时网络是通畅的。
     * @param historyPoints
     */
    private void handleResult(HistoryPoint historyPoints) {
        if (historyPoints == null || historyPoints.getCode() == 400 || historyPoints.getData() == null) {
            if (callback != null) {
                callback.onDataEmpty();
            }
        } else {
            if (callback != null) {
                callback.onNetDataLoaded(historyPoints);
            }
        }
    }

    /**
     * 处理网络错误
     */
    private void handleError() {
        if (callback != null) {
            callback.onNetError();
        }
    }



    private Call<HistoryPoint> getCall(String startTime, String endTime) {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);

        Call<HistoryPoint> searchResult = null;
//        try {
//            String st = URLEncoder.encode(startTime, "UTF-8");
//            String endTime1 = URLEncoder.encode(endTime, "UTF-8");
            searchResult = api.getSearchResult(Const.deviceId, startTime, endTime);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return searchResult;
    }

    @Override
    public void registerCallback(IHistoryCallback callBack) {
        this.callback = callBack;
    }

    @Override
    public void unregisterCallback(IHistoryCallback callback) {
        if (this.callback == callback) {
            this.callback = null;
        }
    }
}
