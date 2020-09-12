package com.shen.baidu.doglost.view;

import com.shen.baidu.doglost.bean.DogCurrentInfo;

/**
 * ui层接口回调。
 */
public interface INetCallBack {
    /**
     * 当数据回来，推给地图上显示
     * @param dogInfo
     */
    void onNetDataLoaded(DogCurrentInfo dogInfo);

    /**
     * 网络错误连接失败，或者异常断开
     */
    void onNetError();

    /**
     * 正常断开。
     */
    void onConnectQuit();

    /**
     * 加载地图中。
     */
    void loading();

    /**
     * 连接成功
     */
    void onNetSuccess();

    /**
     * 心跳回来了
     */
    void heartBeatLoaded();

}
