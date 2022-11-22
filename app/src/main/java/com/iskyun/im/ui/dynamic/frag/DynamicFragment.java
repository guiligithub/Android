package com.iskyun.im.ui.dynamic.frag;

import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.BuildConfig;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.databinding.FragmentDynamicBinding;
import com.iskyun.im.ui.dynamic.ReleaseDynamicsActivity;
import com.iskyun.im.ui.dynamic.TopicDynamicActivity;
import com.iskyun.im.ui.dynamic.viewmodel.DynamicViewModel;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;


public class DynamicFragment extends BaseFragment<FragmentDynamicBinding, DynamicViewModel> {

    private static final String[] DYNAMIC_TITLES = {"动态"};
    private SlidingTabLayout tabLayout;

    public DynamicFragment(){}

    @Override
    public DynamicViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public FragmentDynamicBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentDynamicBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        ImmersionBar.setTitleBar(this, mViewBinding.getRoot().findViewById(R.id.tl_tab_header));
        initFragment();
        TabUtils.setTabLayoutParams(tabLayout);
        mViewBinding.tabHeader.tabIvAction.setImageResource(R.mipmap.icon_camera);
        mViewBinding.tabHeader.tabIvAction.setOnClickListener(v -> {
            if(BuildConfig.DEBUG){
                ActivityUtils.launcher(getActivity(), TopicDynamicActivity.class);
            } else {
                ActivityUtils.launcherForResult(getActivity(),
                        ReleaseDynamicsActivity.DYNAMIC_REQ, ReleaseDynamicsActivity.class);
            }
        });
       mViewBinding.tabHeader.tabs.setTextSelectColor(R.color.bg_center_color);
       mViewBinding.tabHeader.tabs.setIndicatorHeight(0);
    }

    private void initFragment() {
        tabLayout = mViewBinding.getRoot().findViewById(R.id.tabs);
        List<Fragment> fragments = new ArrayList<>();
        int user=0;
        fragments.add(DynamicTabFragment.newInstance(DynamicTabFragment.ALL_DYNAMIC,user));
        TabUtils.init(this, mViewBinding.pager, tabLayout, fragments, DYNAMIC_TITLES);
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}