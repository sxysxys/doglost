package com.shen.baidu.doglost.view;

import com.shen.baidu.doglost.base.IBaseCallback;
import com.shen.baidu.doglost.model.domain.DogCurrentInfo;

/**
 * ui层接口回调。
 */
public interface INetCallBack extends IBaseCallback<DogCurrentInfo> {

    /**
     * 正常断开。
     */
    void onConnectQuit();

    /**
     * 心跳回来了
     */
    void heartBeatLoaded();

}
