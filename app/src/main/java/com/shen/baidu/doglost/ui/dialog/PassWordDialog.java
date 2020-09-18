package com.shen.baidu.doglost.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.utils.PassWordUtil;
import com.shen.baidu.doglost.utils.ToastUtils;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 显示密码弹窗
 */
public class PassWordDialog extends Dialog implements View.OnClickListener  {

    private PassWordDialog.Callback mCallback;

    @BindView(R.id.text_password)
    EditText textPassword;

    @BindView(R.id.button_cancel)
    Button cancelButton;

    @BindView(R.id.button_sure)
    Button sureButton;

    public PassWordDialog(Activity activity, PassWordDialog.Callback callback) {
        super(activity, android.R.style.Theme_Holo_Light_Dialog);
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_password);
        ButterKnife.bind(this);

        cancelButton.setOnClickListener(this);
        sureButton.setOnClickListener(this);
    }

    /**
     * 每次调用show的时候执行
     */
    @Override
    protected void onStart() {
        super.onStart();
        textPassword.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.button_sure:
                // 判断此时的密码是否正确
                String s = textPassword.getText().toString();
                if (s.equals("")) {
                    ToastUtils.showToast("请输入密码!");
                    return;
                }
                if (PassWordUtil.getInstance().isPassWordRight(s)) {
                    mCallback.onSureCallback();
                } else {
                    mCallback.onWrongCallback();
                }
                // 此时符合要求
                dismiss();
                break;
            default:
                break;
        }
    }


    public interface Callback {

        void onSureCallback();

        void onWrongCallback();
    }

}
