package com.iskyun.im.ui.home.frag;

import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.OnlineStatus;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.databinding.FragmentAnchorVideoBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.common.frag.VideoPlayFragment;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.viewmodel.AnchorViewModel;

public class AnchorVideoFragment extends BaseFragment<FragmentAnchorVideoBinding, AnchorViewModel> {

    @Override
    public AnchorViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public FragmentAnchorVideoBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentAnchorVideoBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        LiveDataBus.get().with(Constant.ANCHOR, Anchor.class).observe(this, new Observer<Anchor>() {
            @Override
            public void onChanged(Anchor anchor) {
                setAnchorInfo(anchor);
                createVideoPlayFragment(anchor.getCoverVideoUrl());
            }
        });
    }

    private void createVideoPlayFragment(String path) {
        if(TextUtils.isEmpty(path)){
            return;
        }
        VideoPlayFragment fragment = VideoPlayFragment.newInstance(
                VideoPlayFragment.FROM_ANCHOR_DETAIL, path);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, "video");
        ft.commit();
    }

    private void setAnchorInfo(Anchor anchor){
        mViewBinding.anchorVideoTvUserName.setText(anchor.getNickname());
        mViewBinding.anchorVideoTvAge.setText(String.valueOf(anchor.getAge()));
        mViewBinding.anchorVideoTvSign.setText(anchor.getSignature());
        if(anchor.isOnline() == OnlineStatus.ONLINE.getStatus()){
            mViewBinding.anchorVideoIvOnlineStatus.setImageResource(R.mipmap.icon_anchor_status_on);
        }else{
            mViewBinding.anchorVideoIvOnlineStatus.setImageResource(R.mipmap.icon_anchor_status_off);
        }
        int iconSexResId, bgSexResId;
        if(anchor.getSex() == Sex.WOMAN.ordinal()){
            iconSexResId = R.mipmap.icon_sex_woman;
            bgSexResId = R.mipmap.sex_bg_woman;
        }else {
            iconSexResId = R.mipmap.icon_sex_man;
            bgSexResId = R.mipmap.sex_bg_man;
        }
        mViewBinding.anchorVideoTvAge.setCompoundDrawablesWithIntrinsicBounds(
                DisplayUtils.getDrawable(iconSexResId),null,null,null);
        mViewBinding.anchorVideoTvAge.setBackground(DisplayUtils.getDrawable(bgSexResId));
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}