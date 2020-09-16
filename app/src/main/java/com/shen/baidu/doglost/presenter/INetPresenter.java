package com.shen.baidu.doglost.presenter;

import com.shen.baidu.doglost.base.IBasePresenter;
import com.shen.baidu.doglost.model.domain.SendBean;
import com.shen.baidu.doglost.view.INetCallBack;

/**
 * 提供数据
 */
public interface INetPresenter extends IBasePresenter<INetCallBack> {
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
    void sendData(SendBean dataBean);

}
