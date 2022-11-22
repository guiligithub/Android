package com.iskyun.im.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeui.constants.EaseConstant;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.req.CallRecordBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.databinding.ActivityAnchorInfoBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.helper.SendMsgHelper;
import com.iskyun.im.ui.message.VideoCallActivity;
import com.iskyun.im.ui.frag.AnchorEvaluateFragment;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.ui.message.ChatActivity;
import com.iskyun.im.ui.home.frag.AnchorDetailFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.utils.event.AnchorEvaluateEvent;
import com.iskyun.im.viewmodel.AnchorViewModel;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 个人详情页
 */
public class AnchorInfoActivity extends BaseActivity<ActivityAnchorInfoBinding, AnchorViewModel> {

    private List<Fragment> fragments;
    private Anchor mAnchor;
    private int mSex;

    @Override
    public AnchorViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AnchorViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_tab_header))
                .init();
    }

    @Override
    public ActivityAnchorInfoBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityAnchorInfoBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        setActionImage(R.mipmap.icon_chat_more);
        if (getIntent().getExtras() != null) {
            int userId = getIntent().getExtras().getInt(Constant.ANCHOR_ID);
            mSex = getIntent().getExtras().getInt(Constant.ANCHOR_SEX);
            mViewModel.findAnchorDetailById(userId).observe(this, anchor -> {
                //发送消息
                LiveDataBus.get().with(Constant.ANCHOR).postValue(anchor);
                mAnchor = anchor;
            });
            initFragment();
        }

        mViewBinding.anchorInfoFlChat.setOnClickListener(this::onClick);
        mViewBinding.anchorInfoFlGift.setOnClickListener(this::onClick);
        mViewBinding.anchorInfoLlVideoChat.setOnClickListener(this::onClick);


        //主播评分
        LiveDataBus.get().with(Constant.ANCHOR_EVALUATE, AnchorEvaluateEvent.class).observe(this, new Observer<AnchorEvaluateEvent>() {
            @Override
            public void onChanged(AnchorEvaluateEvent event) {
                AnchorEvaluateFragment fragment = AnchorEvaluateFragment.newInstance(event.getRecordId(),
                        event.getUserId(), mAnchor.getNickname());
                fragment.showNow(getSupportFragmentManager(), fragment.getClass().getSimpleName());
            }
        });
    }

    private void initFragment() {
        fragments = getFragments();
        SlidingTabLayout tabLayout = mViewBinding.getRoot().findViewById(R.id.tabs);
        mViewBinding.pager.setOffscreenPageLimit(fragments.size());
        TabUtils.init(this, mViewBinding.pager, tabLayout, fragments,
                getTitles());
        TabUtils.setTabLayoutParams(tabLayout);
        //if (mSex == Sex.MAN.ordinal()) {
        tabLayout.setVisibility(View.GONE);
        //}

        tabLayout.setOnTabSelectListener(new SlidingTabLayout.OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                changeHeaderColorByIndex(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        /**
         * 如果是主播 加载两个fragment,否则加载 AnchorDetailFragment
         */
      /*  if (mSex == Sex.WOMAN.ordinal()) {
            AnchorVideoFragment videoFragment = new AnchorVideoFragment();
            fragments.add(videoFragment);
        }*/
        AnchorDetailFragment detailFragment = new AnchorDetailFragment();
        fragments.add(detailFragment);
        return fragments;
    }

    private String[] getTitles() {
       /* if (mSex == Sex.WOMAN.ordinal()) {
            return getResources().getStringArray(R.array.anchor_title);
        }*/
        return new String[]{""};
    }

    @Override
    protected void onClick(View view) {
        if (mAnchor == null) {
            return;
        }
        if (view.getId() == R.id.tab_iv_action) {
            MoreActionFragment moreFragment = MoreActionFragment.newInstance(
                    MoreActionFragment.FROM_INFO, mAnchor);
            moreFragment.setOnItemClickListener(this::onItemClick);
            moreFragment.show(getSupportFragmentManager(), "more");
        } else if (view.getId() == R.id.anchor_info_fl_chat) {
            ChatActivity.actionStart(this, "" + mAnchor.getId(),
                    EaseConstant.CHATTYPE_SINGLE);
        } else if (view.getId() == R.id.anchor_info_fl_gift) {
            ChatActivity.actionStart(this, "" + mAnchor.getId(),
                    EaseConstant.CHATTYPE_SINGLE);
        } else if (view.getId() == R.id.anchor_info_ll_video_chat) {
            videoChat();
        }
    }

    /**
     *
     */
    private void videoChat() {
        if (SendMsgHelper.getInstance().isExpireCall(mAnchor, EaseCallType.SINGLE_VIDEO_CALL))
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL,
                    Constant.HX_ID + mAnchor.getId(), null, VideoCallActivity.class);
        //callRecord();
    }

    /**
     * 记录
     */
    private void callRecord() {
        String userId = mAnchor.getId() + "";
        CallRecordBody body = new CallRecordBody();

        int callType, callState;
        callType = 4;
        callState = 1;

        body.setTag(callType);
        body.setCallState(callState);
        body.setTime(0);
        body.setResponse(0);
        body.setUserId(Integer.parseInt(userId));

        UserRepository.get().callRecord(body).enqueue(new Callback<AppResponse<String>>() {
            @Override
            public void onResponse(Call<AppResponse<String>> call, Response<AppResponse<String>> response) {
            }

            @Override
            public void onFailure(Call<AppResponse<String>> call, Throwable t) {
            }
        });
    }

    private void onItemClick(MoreActionFragment.MoreType moreType) {
        switch (moreType) {
            case REPORT:

                break;
            case ATTENTION:
            case UN_ATTENTION:
                attention(RelationType.ATTENTION);
                break;
            case BLOCKLIST:
            case UN_BLOCKLIST:
                attention(RelationType.BLOCK);
                break;
            case MODIFY_REMARK:
                break;
        }
    }

    /**
     * 关注或拉黑
     *
     * @param relationType
     */
    private void attention(RelationType relationType) {
        AnchorDetailFragment fragment = null;
        for (Fragment f : fragments) {
            if (f instanceof AnchorDetailFragment) {
                fragment = ((AnchorDetailFragment) f);
                break;
            }
        }
        if (fragment != null) {
            fragment.attention(relationType);
        }
    }

    private void changeHeaderColorByIndex(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment instanceof AnchorDetailFragment) {
            ((AnchorDetailFragment) fragment).changeHeaderColor();
        } else {
            changeHeaderColor(true);
        }
    }

    public void changeHeaderColor(boolean defaultHeader) {
        if (defaultHeader) {
            mViewBinding.tabHeader.tlTabHeader.setBackground(null);
        } else {
            mViewBinding.tabHeader.tlTabHeader.setBackground(null);
            //mViewBinding.tabHeader.tlTabHeader.setBackground(DisplayUtils.getDrawable(R.drawable.shape));
        }
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.TabHeader;
    }

    /**
     * AnchorInfoActivity 启动
     *
     * @param userId
     * @param sex
     */
    public static void launcher(int userId, int sex) {
        Bundle args = new Bundle();
        args.putInt(Constant.ANCHOR_ID, userId);
        args.putInt(Constant.ANCHOR_SEX, sex);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), args, AnchorInfoActivity.class);
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}