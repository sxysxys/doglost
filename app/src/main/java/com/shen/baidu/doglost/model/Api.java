package com.shen.baidu.doglost.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.shen.baidu.doglost.model.domain.HistoryPoint;
import com.shen.baidu.doglost.model.domain.ResponseLogin;

/**
 * 使用retrofit框架拿数据
 */
public interface Api {

    /**
     * 获取历史轨迹信息
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @GET("device/record")
    Call<HistoryPoint> getSearchResult(@Query("deviceId") int deviceId,
                                       @Query("startTime") String startTime,
                                       @Query("endTime") String endTime);

    /**
     * 登录验证
     * @param loginName
     * @param loginPwd
     * @return
     */
    @GET("userApp/validated")
    Call<ResponseLogin> login(@Query("loginName") String loginName,
                              @Query("loginPwd") String loginPwd);





}
