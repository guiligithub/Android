package com.iskyun.im.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityMineBroeseBinding;
import com.iskyun.im.ui.home.HomeViewModel;
import com.iskyun.im.ui.mine.frag.FansFragment;
import com.iskyun.im.ui.mine.frag.FollowFragment;
import com.iskyun.im.ui.mine.frag.VisitorFragment;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends BaseActivity<ActivityMineBroeseBinding, HomeViewModel> {
    private List<Fragment> fragments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_tab_header))
                .init();
    }

    @Override
    public HomeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(HomeViewModel.class);
    }

    @Override
    public ActivityMineBroeseBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityMineBroeseBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        initFragment();
    }

    private void initFragment() {
        SlidingTabLayout tabLayout = mViewBinding.getRoot().findViewById(R.id.tabs);

        TabUtils.setTabLayoutParam(tabLayout);
        List<Fragment> fragments = new ArrayList<>();
        VisitorFragment visitorFragment = new VisitorFragment();
        FansFragment fansFragment = new FansFragment();
        FollowFragment followFragment = new FollowFragment();
        fragments.add(visitorFragment);
        fragments.add(fansFragment);
        fragments.add(followFragment);
        TabUtils.init(this, mViewBinding.pager, tabLayout, fragments,
                getResources().getStringArray(R.array.browse_title));
        mViewBinding.pager.setOffscreenPageLimit(1);
        int id=getIntent().getIntExtra("id",0);
        mViewBinding.pager.setCurrentItem(id);

    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.TabHeader;
    }

    @Override
    public int getTitleText() {
        return 0;
    }

}
