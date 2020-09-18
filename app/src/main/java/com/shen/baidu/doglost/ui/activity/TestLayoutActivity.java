package com.shen.baidu.doglost.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.adapter.TestFragmentAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestLayoutActivity extends AppCompatActivity {

    @BindView(R.id.segment_tab)
    SegmentTabLayout tabLayout;

    @BindView(R.id.sliding_tab)
    SlidingTabLayout slidingTabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        tabLayout.setTabData(new String[]{"123","456"});
        TestFragmentAdapter adapter = new TestFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        slidingTabLayout.setViewPager(viewPager, new String[]{"123", "456", "567"});

    }
}