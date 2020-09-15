package com.shen.baidu.doglost.presenter;

import com.shen.baidu.doglost.base.IBasePresenter;
import com.shen.baidu.doglost.view.IHistoryCallback;

public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {
    void getHisPoint(int deviceId, String startTime, String endTime);
}
