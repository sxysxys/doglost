package com.shen.baidu.doglost.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.constant.Const;
import com.shen.baidu.doglost.constant.FenceShape;


/**
 * 创建围栏参数设置，将参数带回去
 */
public class CreateFenceOptions extends AppCompatActivity implements View.OnClickListener {

    private View vertexesNumberLayout;
    private Button cancelBtn;
    private Button sureBtn;
    private RadioButton circleBtn = null;
    private RadioButton polygonBtn = null;


    private EditText vertexesNumberText = null;

    private FenceShape fenceShape = FenceShape.circle;

    // 返回结果
    private Intent result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_options);
        //
        vertexesNumberLayout = findViewById(R.id.layout_vertexes_number);
        //
        cancelBtn = findViewById(R.id.btn_cancel);
        //
        sureBtn = findViewById(R.id.btn_sure);
        //
        circleBtn = findViewById(R.id.btn_circle);
        //
        polygonBtn = findViewById(R.id.btn_polygon);
        //
        vertexesNumberText = findViewById(R.id.text_vertexes_number);


        cancelBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
        circleBtn.setOnClickListener(this);
        polygonBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_circle:
                fenceShape = FenceShape.circle;
                vertexesNumberLayout.setVisibility(View.GONE);
                break;


            case R.id.btn_polygon:
                fenceShape = FenceShape.polygon;
                vertexesNumberLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_cancel:
                onCancel(v);
                break;

            case R.id.btn_sure:
                String vertexesNumberStr = vertexesNumberText.getText().toString();
                int vertexesNumber = 3;
                if (!TextUtils.isEmpty(vertexesNumberStr)) {
                    try {
                        vertexesNumber = Integer.parseInt(vertexesNumberStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                result = new Intent();
                result.putExtra("fenceShape", fenceShape.name());
                result.putExtra("vertexesNumber", vertexesNumber);
                setResult(Const.RESULT_CODE, result);
                super.onBackPressed();
                break;

            default:
                break;
        }
    }

    public void onCancel(View v) {
        super.onBackPressed();
    }

}
