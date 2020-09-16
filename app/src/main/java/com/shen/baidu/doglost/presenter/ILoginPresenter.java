package com.shen.baidu.doglost.presenter;

import com.shen.baidu.doglost.base.IBasePresenter;
import com.shen.baidu.doglost.view.ILoginInfoCallback;

public interface ILoginPresenter extends IBasePresenter<ILoginInfoCallback> {
    void loginRequest(String loginName, String loginPassword);
}
