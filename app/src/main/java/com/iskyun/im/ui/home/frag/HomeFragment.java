package com.iskyun.im.ui.home.frag;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.data.bean.Tag;
import com.iskyun.im.databinding.FragmentHomeBinding;
import com.iskyun.im.ui.home.HomeViewModel;
import com.iskyun.im.ui.home.SearchActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {


    public HomeFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public HomeViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public FragmentHomeBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentHomeBinding.inflate(inflater);
    }


    @Override
    public void initBase() {
        initFragment();
        ImmersionBar.setTitleBar(this, mViewBinding.getRoot().findViewById(R.id.tl_tab_header));
        mViewBinding.tabHeader.tabIvAction.setImageResource(R.mipmap.icon_search_light);
        mViewBinding.tabHeader.tabIvAction.setOnClickListener(v -> {
            ActivityUtils.launcher(this.getActivity(), SearchActivity.class);
        });
    }

    private void initFragment() {
        List<String> titles = new ArrayList<>();
        titles.add(Tag.NEWS.getTitle());
        //titles.add(Tag.NEARBY.getTitle());
        titles.add(Tag.RECOMMEND.getTitle());
        //titles.add(Tag.FIVE.getTitle());
        //titles.add(Tag.FOUR.getTitle());
        //titles.add(Tag.THREE.getTitle());

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeTabFragment.newInstance(Tag.NEWS.getTag()));
        //fragments.add(new NearbyTabFragment());
        fragments.add(HomeTabFragment.newInstance(Tag.RECOMMEND.getTag()));
        //fragments.add(HomeTabFragment.newInstance(Tag.FIVE.getTag()));
        //fragments.add(HomeTabFragment.newInstance(Tag.FOUR.getTag()));
        //fragments.add(HomeTabFragment.newInstance(Tag.THREE.getTag()));
        SlidingTabLayout tabLayout = mViewBinding.getRoot().findViewById(R.id.tabs);
        TabUtils.setTabLayoutParams(tabLayout);
        TabUtils.init(this, mViewBinding.pager, tabLayout, fragments, titles.toArray(new String[]{}));
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}