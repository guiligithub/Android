package com.iskyun.im.ui.message.frag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.FragmentChatTopBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.common.FeedbackActivity;
import com.iskyun.im.ui.frag.ContentDialogFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.event.DiamondChangeEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;

public class ChatTopFragment extends BaseFragment<FragmentChatTopBinding, AnchorViewModel> {

    private Anchor anchor;

    @Override
    public AnchorViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AnchorViewModel.class);
    }

    @Override
    public FragmentChatTopBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentChatTopBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        setDiamondText();
        setIntimacyText(0);

        String notice = "公告：加微信裸聊，要求到其他平台，要求转账约会都是欺诈行为，请勿相信";
        mViewBinding.chatHeaderTvNotice.setText(notice);

        LiveDataBus.get().with(Constant.DIAMOND_CHANGE, DiamondChangeEvent.class).observe(this, new Observer<DiamondChangeEvent>() {
            @Override
            public void onChanged(DiamondChangeEvent diamondChangeEvent) {
                setDiamondText();
            }
        });

        LiveDataBus.get().with(Constant.CHAT_ANCHOR, Anchor.class).observe(this, a -> {
            anchor = a;
            setIntimacyText(a.getIntimateNum());
        });

        mViewBinding.chatHeaderTvWx.setOnClickListener(v -> lookWechatNumDialog());
        mViewBinding.chatHeaderTvReport.setOnClickListener(v -> {
            if(anchor != null)
                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), FeedbackActivity.class);
        });

        mViewModel.observerWeChat().observe(this, s ->{
            showWechatDialog(s);
            if (anchor.isUnlockWechat() == 0) {
                User user = SpManager.getInstance().getCurrentUser();
                user.setUserDiamond(user.getUserDiamond() - anchor.getWechatPrice());
                SpManager.getInstance().setCurrentUser(user);
            }
        });

        User user = SpManager.getInstance().getCurrentUser();
        if(user != null && user.getSex() == Sex.WOMAN.ordinal()){
            mViewBinding.chatHeaderTvWx.setVisibility(View.GONE);
        }
    }

    private void setIntimacyText(double intimacy) {
        String intimacyContent = getString(R.string.intimacy) + ":" + intimacy;
        mViewBinding.chatHeaderTvIntimacy.setText(intimacyContent);
    }

    private void setDiamondText() {
        User user = SpManager.getInstance().getCurrentUser();
        String diamond = getString(R.string.diamond);
        String diamondText = diamond + ":" + user.getUserDiamond();
        SpannableString sContent = new SpannableString(diamondText);
        sContent.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                diamond.length() + 1, diamondText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mViewBinding.chatHeaderTvDiamond.setText(sContent);
    }

    private void lookWechatNumDialog() {
        if(anchor == null)
            return;
        String lookWeChat;
        int isAnchor = anchor.isAnchor();
        //非主播返回
        if (isAnchor != 1) {
            return;
        }

        if (anchor.isUnlockWechat() == 1) {
            lookWeChat = getString(R.string.looked_wechat_content_tips);
            lookWeChat = String.format(lookWeChat, anchor.getNickname());
        } else {
            lookWeChat = getString(R.string.look_wechat_content_tips);
            lookWeChat = String.format(lookWeChat, anchor.getNickname(), anchor.getWechatPrice());
        }
        ContentDialogFragment.Builder builder = new ContentDialogFragment.Builder(getActivity());

        builder.setContent(lookWeChat)
                .setConfirmColor(R.color.colorAccent)
                .setOnConfirmClickListener(getString(R.string.confirm), view -> {
                    lookWechat();
                })
                .showCancelButton(true);
        builder.show();
    }

    private void lookWechat() {
        User user = SpManager.getInstance().getCurrentUser();
        if (user.getUserDiamond() < anchor.getWechatPrice()) {
            ToastUtils.showToast(R.string.balance_is_insufficient);
            return;
        }
        mViewModel.getWeChatNum(anchor.getId());
    }

    private void showWechatDialog(String wechatNumber) {
        if(anchor != null){
            anchor.setIsUnlockWechat(1);
        }
        ContentDialogFragment.Builder builder = new ContentDialogFragment.Builder((BaseActivity) getActivity());
        builder.setContent(wechatNumber)
                .setOnConfirmClickListener(getString(R.string.copy), view -> {
                    copyWechat(wechatNumber);
                })
                .showCancelButton(true);
        builder.show();
    }

    private void copyWechat(String wechatNumber) {
        try {
            ClipboardManager cm = (ClipboardManager) getActivity()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("label", wechatNumber);
            cm.setPrimaryClip(mClipData);
            ToastUtils.showToast(R.string.copy_success);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }

}