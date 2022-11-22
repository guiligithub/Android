package com.iskyun.im.utils;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.iskyun.im.adapter.AppPagerAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.List;

public class TabUtils {

    public static void init(Fragment fragment, ViewPager2 pager, SlidingTabLayout tab,
                            List<Fragment> fragments, String[] titles) {
        AppPagerAdapter adapter = new AppPagerAdapter(fragment, fragments);
        init(adapter, pager, tab, titles);
    }

    public static void init(FragmentActivity activity, ViewPager2 pager, SlidingTabLayout tab,
                            List<Fragment> fragments, String[] titles) {
        AppPagerAdapter adapter = new AppPagerAdapter(activity, fragments);
        init(adapter, pager, tab, titles);
    }

    public static void activityinit(Fragment fragment,ViewPager2 pager, SlidingTabLayout tab,
                            List<Fragment>fragments, String[] titles){
        AppPagerAdapter adapter = new AppPagerAdapter(fragment,
                fragments);
        pager.setAdapter(adapter);
        tab.setViewPager(pager,titles);
        pager.setCurrentItem(0);
        View child = pager.getChildAt(0);
        if (child instanceof RecyclerView){
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }



    /**
     * 修改布局参数
     *
     * @param tabLayout
     */
    public static void setTabLayoutParams(SlidingTabLayout tabLayout) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabLayout.getLayoutParams();
        params.width = DisplayUtils.dp2px(100);
        params.removeRule(RelativeLayout.START_OF);
        tabLayout.setLayoutParams(params);
    }

    public static void setTabLayoutParam(SlidingTabLayout tabLayout) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabLayout.getLayoutParams();
        params.width = DisplayUtils.dp2px(200);
        params.removeRule(RelativeLayout.START_OF);
        tabLayout.setLayoutParams(params);
    }

    private static void init(AppPagerAdapter adapter, ViewPager2 pager, SlidingTabLayout tab, String[] titles) {
        pager.setAdapter(adapter);
        tab.setViewPager(pager, titles);
        pager.setCurrentItem(0);
        View child = pager.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }
}
