package com.shen.baidu.doglost.base;


public interface IBasePresenter<T> {
    /**
     * 注册
     * @param callBack
     */
    void registerCallback(T callBack);

    /**
     * 取消注册
     * @param callback
     */
    void unregisterCallback(T callback);
}
