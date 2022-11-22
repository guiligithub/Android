package com.iskyun.im.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class AppPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    public AppPagerAdapter(Fragment fragment, List<Fragment> fragments){
        super(fragment);
        this.fragments = fragments;
    }

    public AppPagerAdapter(FragmentActivity fragment, List<Fragment> fragments){
        super(fragment);
        this.fragments = fragments;
    }


    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
