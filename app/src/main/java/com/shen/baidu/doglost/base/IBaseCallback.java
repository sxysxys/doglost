package com.shen.baidu.doglost.base;

/**
 * 基础回调接口
 * @param <T>
 */
public interface IBaseCallback<T> {

    /**
     * 数据成功加载
     * @param data
     */
    void onNetDataLoaded(T data);

    /**
     * 网络没问题，但是拿回的数据为空。
     */
    void onDataEmpty();

    /**
     *  网络错误
     */
    void onNetError();

    /**
     * 加载中。
     */
    void loading();

    /**
     * 连接成功
     */
    void onNetSuccess();
}
