package com.shen.baidu.doglost.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shen.baidu.doglost.ui.fragment.TestFragment;

/**
 * @Author: shenge
 * @Date: 2020/9/18 15:36
 */
public class TestFragmentAdapter extends FragmentPagerAdapter {


    public TestFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new TestFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return "123";
//    }
}
