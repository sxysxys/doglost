package com.shen.baidu.doglost.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectTimeDialog extends Dialog implements View.OnClickListener  {

    private SelectTimeDialog.Callback mCallback;

    @BindView(R.id.start_time)
    Button startButton;

    @BindView(R.id.end_time)
    Button stopButton;

    @BindView(R.id.button_cancel)
    Button cancelButton;

    @BindView(R.id.button_sure)
    Button sureButton;

    Date startTime;

    Date endTime;

    private int tag = 0;

    public SelectTimeDialog(Context activity, SelectTimeDialog.Callback callback) {
        super(activity, android.R.style.Theme_Holo_Light_Dialog);
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trackquery_options);
        ButterKnife.bind(this);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        sureButton.setOnClickListener(this);
    }

    /**
     * 每次调用show的时候执行
     */
    @Override
    protected void onStart() {
        super.onStart();
        startTime = null;
        endTime = null;
        tag = 0;
        startButton.setText(null);
        stopButton.setText(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                dismiss();
                mCallback.onCancelCallback();
                break;
            case R.id.start_time:
                // 此时需要弹出框
                tag = 1;
                showTimeDialog();
                break;
            case R.id.end_time:
                tag = 2;
                showTimeDialog();
                break;
            case R.id.button_sure:
                if (startTime == null) {
                    ToastUtils.showToast("请输入起始时间");
                    break;
                }
                if (endTime == null) {
                    ToastUtils.showToast("请输入结束时间");
                    break;
                }
                if (startTime.compareTo(endTime) != -1) {
                    ToastUtils.showToast("输入的时间有误");
                    break;
                }
                // 此时符合要求
                dismiss();
                mCallback.onSureCallback(date2String(startTime), date2String(endTime));
                break;
            default:
                break;
        }
    }

    /**
     * 显示时间弹窗。
     */
    private void showTimeDialog() {
        DatePickDialog dialog = new DatePickDialog(getContext());
        //设置上下年分限制
        dialog.setYearLimt(5);
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(DateType.TYPE_ALL);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd HH:mm");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                // 说明此时的是开始时间的输入
                if (tag == 1) {
                    SelectTimeDialog.this.startTime = date;
                    startButton.setText(date2String(startTime));
                }
                // 说明此时是结束时间的输入
                if (tag == 2) {
                    SelectTimeDialog.this.endTime = date;
                    stopButton.setText(date2String(endTime));
                }
            }
        });
        dialog.show();
    }

    private String date2String(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public interface Callback {

        void onSureCallback(String startTime, String endTime);

        void onCancelCallback();
    }

}
