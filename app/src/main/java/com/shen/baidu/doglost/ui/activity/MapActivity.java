package com.shen.baidu.doglost.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.shen.baidu.doglost.R;
import com.shen.baidu.doglost.ui.fragment.HistoryFragment;
import com.shen.baidu.doglost.ui.fragment.MapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MapActivity extends AppCompatActivity {

    private Unbinder mBind;

    @BindView(R.id.segment_tab)
    SegmentTabLayout segmentTabLayout;

    private MapFragment mMapFragment;

    private HistoryFragment mHistoryFragment;
    private FragmentManager fragmentManager;

    private Fragment lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layout);
        initFragments();
        initView();
        initListener();
    }

    private void initListener() {
        segmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        switchFragment(mMapFragment);
                        break;
                    case 1:
                        switchFragment(mHistoryFragment);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            transaction.add(R.id.map_page_container, fragment);
        } else {
            transaction.show(fragment);
        }
        if (lastFragment != null && lastFragment != fragment) {
            transaction.hide(lastFragment);
        }
        lastFragment = fragment;
        transaction.commit();
    }

    private void initFragments() {
        mHistoryFragment = new HistoryFragment();
        mMapFragment = new MapFragment();
        lastFragment = mMapFragment;
    }

    private void initView() {
        mBind = ButterKnife.bind(this);

        segmentTabLayout.setTabData(new String[]{"定位", "轨迹追踪"});
        // 初始化fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.map_page_container, mMapFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}