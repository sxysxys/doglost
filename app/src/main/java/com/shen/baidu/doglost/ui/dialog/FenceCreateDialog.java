package com.shen.baidu.doglost.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.constant.FenceShape;

/**
 * 围栏创建对话框
 */
public class FenceCreateDialog extends Dialog implements View.OnClickListener {

    /**
     * 回调接口
     */
    private Callback callback;

    private View fenceRadiusLayout;
    private Button cancelBtn;
    private Button sureBtn;
    private TextView titleText;
    private EditText fenceRadiusText;

    private FenceShape fenceShape = FenceShape.circle;

    /**
     * 默认圆形围栏参数
     */
    private double radius = 100;

    public FenceCreateDialog(Context activity, Callback callback) {
        super(activity, android.R.style.Theme_Holo_Light_Dialog);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fence_create);

        fenceRadiusLayout = findViewById(R.id.layout_fenceCreate_radius);
        titleText = (TextView) findViewById(R.id.tv_fenceCreate_title);

        fenceRadiusText = (EditText) findViewById(R.id.edtTxt_fenceCreate_radius);

        cancelBtn = (Button) findViewById(R.id.btn_fenceCreate_cancel);
        sureBtn = (Button) findViewById(R.id.btn_fenceCreate_sure);
        cancelBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
    }

    /**
     * 在调用show()的时候进入这个方法。
     */
    @Override
    protected void onStart() {
        switch (fenceShape) {
            case circle:
                fenceRadiusLayout.setVisibility(View.VISIBLE);
                break;

            case polygon:
                titleText.setText(R.string.fence_create_polygon);
                fenceRadiusLayout.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fenceCreate_cancel:
                dismiss();
                if (null != callback) {
                    callback.onCancelCallback();
                }
                break;

            case R.id.btn_fenceCreate_sure:
                String radiusStr = fenceRadiusText.getText().toString();

                if (!TextUtils.isEmpty(radiusStr)) {
                    try {
                        radius = Double.parseDouble(radiusStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (null != callback) {
                    callback.onSureCallback(radius);
                }
                dismiss();
                break;

            default:
                break;
        }
    }

    public void setFenceShape(FenceShape fenceShape) {
        this.fenceShape = fenceShape;
    }

    /**
     * 创建回调接口
     */
    public interface Callback {
        /**
         * 确定回调
         */
        void onSureCallback(double radius);

        /**
         * 取消回调
         */
        void onCancelCallback();
    }

}
