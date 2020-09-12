package com.shen.baidu.doglost.utils;

import android.widget.Toast;

import com.shen.baidu.doglost.DemoApplication;

/**
 * 弹框的工具类
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToast(String tip) {
        if (toast == null) {
            toast = Toast.makeText(DemoApplication.getContext(), tip, Toast.LENGTH_SHORT);
        } else {
            toast.setText(tip);
        }
        toast.show();
    }
}
