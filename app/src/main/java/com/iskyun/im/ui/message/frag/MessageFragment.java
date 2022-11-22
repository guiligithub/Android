package com.iskyun.im.ui.message.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.databinding.FragmentMessageBinding;
import com.iskyun.im.ui.message.viewmodel.MessageViewModel;
import com.iskyun.im.ui.mine.frag.FollowFragment;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.viewmodel.GiftViewModel;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseFragment<FragmentMessageBinding, MessageViewModel> {

    private static final String[] MSG_TITLES = {"消息", "关注"};
    private SlidingTabLayout tabLayout;
    private boolean off = true;

    public MessageFragment(){}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取off
    }

    @Override
    public MessageViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(MessageViewModel.class);
    }

    @Override
    public FragmentMessageBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentMessageBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        ImmersionBar.setTitleBar(this, mViewBinding.getRoot().findViewById(R.id.tl_tab_header));
        initFragment();
        TabUtils.setTabLayoutParams(tabLayout);

        mViewBinding.tabHeader.tabIvAction.setVisibility(View.INVISIBLE);
        mViewBinding.tabHeader.tabIvAction.setImageTintList(null);
        setOff();
        mViewBinding.tabHeader.tabIvAction.setOnClickListener(v -> {
            setOff();
            off = !off;
            //存off
        });

        loadGift();
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    private void initFragment() {
        tabLayout = mViewBinding.getRoot().findViewById(R.id.tabs);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MsgTabFragment());
        fragments.add(new FollowFragment());
        TabUtils.init(this, mViewBinding.pager, tabLayout, fragments, MSG_TITLES);
    }

    private void setOff() {
        if (off) {
            mViewBinding.tabHeader.tabIvAction.setImageResource(R.mipmap.icon_ping_pong_off);
        } else {
            mViewBinding.tabHeader.tabIvAction.setImageResource(R.mipmap.icon_ping_pong_on);
        }
    }

    /**
     * 预加载gift
     */
    private void loadGift() {
        GiftViewModel giftViewModel = new ViewModelProvider(this).get(GiftViewModel.class);
        giftViewModel.setShowDialog(false);
        giftViewModel.getGifts().observe(this, giftType -> {
            if (!giftType.getCommonGift().isEmpty()) {
                for (Gift gift : giftType.getCommonGift()) {
                    if(gift.isSpecialEffects() == 1){
                        if(gift.getSpecialPic().endsWith(".gif")){
                            Glide.with(this).asGif().load(gift.getSpecialPic()).preload();
                        }
                    }
                }
            }
            if (!giftType.getVipGift().isEmpty()) {
                for (Gift gift : giftType.getVipGift()) {
                    if(gift.isSpecialEffects() == 1){
                        if(gift.getSpecialPic().endsWith(".gif")){
                            Glide.with(this).asGif().load(gift.getSpecialPic()).preload();
                        }
                    }
                }
            }
        });
    }

}