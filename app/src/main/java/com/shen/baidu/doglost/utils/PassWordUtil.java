package com.shen.baidu.doglost.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.shen.baidu.doglost.DemoApplication;
import com.shen.baidu.doglost.constant.Const;

import java.util.PropertyResourceBundle;

/**
 * @Author: shenge
 * @Date: 2020/9/16 20:21
 */
public class PassWordUtil {

    private static PassWordUtil passWordUtil;

    // 存储相应的信息
    private SharedPreferences mSharedPreferences;

    private PassWordUtil() {
        mSharedPreferences = DemoApplication.getContext().getSharedPreferences(Const.USER_INFO, Context.MODE_PRIVATE);
    }

    public static PassWordUtil getInstance() {
        if (passWordUtil == null) {
            passWordUtil = new PassWordUtil();
        }
        return passWordUtil;
    }

    /**
     * 保存
     * @param loginName
     * @param loginPassword
     */
    public void save(String loginName, String loginPassword) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString("loginName", loginName);
        edit.putString("loginPassWord", loginPassword);
        edit.apply();
    }

    /**
     * 判断用户此时输入的密码正确性
     * @param passWord
     * @return
     */
    public boolean isPassWordRight(String passWord) {
        String loginPassWord = mSharedPreferences.getString("loginPassWord", null);
        if (passWord.equals(loginPassWord)) {
            return true;
        }
        return false;
    }


}
