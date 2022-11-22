package com.iskyun.im.ui.common;

import android.content.Intent;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityFeedbackBinding;
import com.iskyun.im.ui.common.frag.FeedbackFragment;
import com.iskyun.im.ui.common.frag.ReportFragment;
import com.iskyun.im.ui.common.viewmodel.ComplaintViewModel;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends BaseActivity<ActivityFeedbackBinding, ComplaintViewModel> {
    private List<Fragment> fragments = new ArrayList<>();
    private int FragmentPosition=0;

    @Override
    public ComplaintViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(ComplaintViewModel.class);
    }

    @Override
    public ActivityFeedbackBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityFeedbackBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        initFragment();

    }

    private void initFragment() {
        ReportFragment reportFragment=new ReportFragment();
        FeedbackFragment feedbackFragment =new FeedbackFragment();
        fragments.add(reportFragment);
        fragments.add(feedbackFragment);
        TabUtils.init(this, mViewBinding.feedbackVPager, mViewBinding.feedbackTab,
                fragments, new String[]{getString(R.string.mine_complaint),
                        getString(R.string.suggestions)});
        mViewBinding.feedbackTab.setOnTabSelectListener(new SlidingTabLayout.OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                FragmentPosition=position;
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       Fragment fragment= fragments.get(FragmentPosition);
       fragment.onActivityResult(requestCode, resultCode, data);
       super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public int getTitleText() {
        return  R.string.feedback;
    }
}
