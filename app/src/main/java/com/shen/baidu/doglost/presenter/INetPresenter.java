package com.shen.baidu.doglost.presenter;

import com.shen.baidu.doglost.bean.MsgDataBean;
import com.shen.baidu.doglost.view.INetCallBack;

/**
 * 提供数据
 */
public interface INetPresenter {
    /**
     * 调用连接。
     */
    void firstConnect();

    /**
     * 调用连接
     */
    void connect();

    /**
     * 删除连接
     */
    void delConnect();

    /**
     * 发送数据
     */
    void sendData(MsgDataBean dataBean);
    /**
     * 注册
     * @param callBack
     */
    void registerCallback(INetCallBack callBack);

    /**
     * 取消注册
     * @param callback
     */
    void unregisterCallback(INetCallBack callback);
}
