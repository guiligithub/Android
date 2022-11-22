package com.iskyun.im.ui.home.frag;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.ImageBannerAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.ImageBannerBean;
import com.iskyun.im.data.bean.OnlineStatus;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.databinding.FragmentAnchorDetailBinding;
import com.iskyun.im.databinding.ViewAnchorDetailTagBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.frag.ContentDialogFragment;
import com.iskyun.im.ui.home.AnchorInfoActivity;
import com.iskyun.im.ui.common.PreviewPicturesActivity;
import com.iskyun.im.ui.mine.MineDynamicActivity;
import com.iskyun.im.ui.mine.MyGiftActivity;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;
import com.iskyun.im.viewmodel.RelationViewModel;
import com.iskyun.im.widget.SkyTextView;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class AnchorDetailFragment extends BaseFragment<FragmentAnchorDetailBinding, AnchorViewModel> {

    private int currentScrollY = 0;
    private int scrollYLocation;
    private RelationViewModel relationViewModel;
    private RelationType relationType;
    private Anchor anchor;
    private ImageBannerAdapter bannerAdapter;

    @Override
    public AnchorViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AnchorViewModel.class);
    }

    @Override
    public FragmentAnchorDetailBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentAnchorDetailBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        getScrollYLocation();
        initData(null);
        initListener();
        LiveDataBus.get().with(Constant.ANCHOR, Anchor.class).observe(this, this::initData);
        relationViewModel = onCreateViewModel(RelationViewModel.class);

        relationViewModel.observerRelation().observe(this, s -> {
            switch (relationType) {
                case BLOCK:
                    if (anchor != null) {
                        if (anchor.isBlack() == 1) {
                            anchor.setBlack(0);
                        } else {
                            anchor.setBlack(1);
                        }
                    }
                    break;
                case ATTENTION:
                    if (anchor != null) {
                        if (anchor.isFocus() == 1) {
                            anchor.setFocus(0);
                        } else {
                            anchor.setFocus(1);
                        }
                        if (anchor.isFocus() == 1) {
                            mViewBinding.anchorDetailTvAttention.setText(R.string.followed);//关注
                        } else {
                            mViewBinding.anchorDetailTvAttention.setText(R.string.attention);
                        }
                        mViewBinding.anchorDetailTvAttention.setSelected(anchor.isFocus() == 1);
                    }
                    break;
            }
        });

        //mViewBinding.anchorDetailTvInfoWx.setText(s);
        mViewModel.observerWeChat().observe(this, s -> {
            showWechatDialog(s);
            if (anchor.isUnlockWechat() == 0) {
                User user = SpManager.getInstance().getCurrentUser();
                user.setUserDiamond(user.getUserDiamond() - anchor.getWechatPrice());
                SpManager.getInstance().setCurrentUser(user);
            }
        });
    }

    private void initData(@Nullable Anchor anchor) {
        if (anchor != null) {
            this.anchor = anchor;
        }
        setChatPrice(anchor);
        setBaseInfo(anchor);
        setAlbums(anchor);
        setTags(anchor);
        setEvaluations(anchor);
    }

    private void initListener() {
        mViewBinding.anchorDetailTvAttention.setOnClickListener(this::onClick);//关注点击
        // mViewBinding.anchorDetailIvAccost.setOnClickListener(this::onClick);//搭讪点击
        mViewBinding.anchorDetailTvInfoWx.setOnClickListener(this::onClick);//查看微信
        // mViewBinding.anchorDetailTvReport.setOnClickListener(this::onClick);//举报
        mViewBinding.anchorDetailLlDynamic.setOnClickListener(this::onClick);//举报
        mViewBinding.anchorDetailLlGift.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        int id = view.getId();
        if (id == R.id.anchor_detail_tv_attention) {
            attention(RelationType.ATTENTION);
        } else if (id == R.id.anchor_detail_tv_info_wx) {
            lookWechatNumDialog();
        } else if (id == R.id.anchor_detail_ll_dynamic) {
            if (anchor != null)
                MineDynamicActivity.launcher(anchor.getId());
        } else if (id == R.id.anchor_detail_ll_gift) {
            if (anchor != null)
                MyGiftActivity.launcher(anchor.getId(), anchor.getNickname(), anchor.getHeadUrl());
        }

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


    private void lookWechatNumDialog() {
        String lookWeChat;
        if (anchor.isUnlockWechat() == 1) {
            lookWeChat = getString(R.string.looked_wechat_content_tips);
            lookWeChat = String.format(lookWeChat, anchor.getNickname());
        } else {
            lookWeChat = getString(R.string.look_wechat_content_tips);
            lookWeChat = String.format(lookWeChat, anchor.getNickname(), anchor.getWechatPrice());
        }
        ContentDialogFragment.Builder builder = new ContentDialogFragment.Builder((BaseActivity) getActivity());

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


    public void changeHeaderColor() {
        if (currentScrollY != 0) {
            changeHeaderColor(currentScrollY);
        }
    }

    /**
     * 关注  拉黑
     */
    public void attention(RelationType relationType) {
        if (this.anchor == null)
            return;
        User user = SpManager.getInstance().getCurrentUser();
        this.relationType = relationType;
        RelationBody body = new RelationBody(relationType.getRelationType(),
                user.getId(), this.anchor.getId());
        switch (relationType) {
            case BLOCK:
                try {
                    if (this.anchor.isBlack() == 1) {
                        relationViewModel.delAttention(body);
                        EMClient.getInstance().contactManager()
                                .removeUserFromBlackList(Constant.HX_ID + this.anchor.getId());
                    } else {
                        EMClient.getInstance().contactManager().addUserToBlackList(
                                Constant.HX_ID + this.anchor.getId(), true);
                        relationViewModel.addUserRelation(body);
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                break;
            case ATTENTION:
                if (this.anchor.isFocus() == 1) {
                    relationViewModel.delAttention(body);
                } else {
                    relationViewModel.addUserRelation(body);
                }
                break;
        }
    }

    /**
     * 改变头部颜色
     */
    private void changeHeaderColor(int scrollY) {
        AnchorInfoActivity activity = (AnchorInfoActivity) getActivity();
        if (activity != null) {
            activity.changeHeaderColor(scrollY <= scrollYLocation);
        }
    }


    /**
     * 获取滑动改变头部颜色的位置
     */
    private void getScrollYLocation() {
        mViewBinding.nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            currentScrollY = oldScrollY;
            changeHeaderColor(scrollY);
        });

/*        mViewBinding.anchorDetailRsvStar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                mViewBinding.anchorDetailRsvStar.getLocationOnScreen(location);
                scrollYLocation = location[1];
                mViewBinding.anchorDetailRsvStar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/
    }


    /**
     * 聊天价格
     */
    private void setChatPrice(@Nullable Anchor anchor) {
        String text = DisplayUtils.getString(R.string.chat_price);
        int msgPrice = 0, videoPrice = 0, voicePrice = 0;
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null && user.getSex() == Sex.WOMAN.ordinal()) {
            mViewBinding.anchorDetailLlPrice.setVisibility(View.GONE);
        } else {
            mViewBinding.anchorDetailLlPrice.setVisibility(View.VISIBLE);
        }
        if (anchor != null) {
            msgPrice = anchor.getTextPrice();
            videoPrice = anchor.getVideoMinute();
            voicePrice = anchor.getVoiceMinute();

            //聊天价格
            /*if (anchor.getSex() == Sex.WOMAN.ordinal()) {
                mViewBinding.anchorDetailLlPrice.setVisibility(View.VISIBLE);
            } else {
                mViewBinding.anchorDetailLlPrice.setVisibility(View.GONE);
            }*/
            // mViewBinding.anchorDetailTvMsgPrice.setText(String.format(text, msgPrice));
            mViewBinding.anchorDetailTvVideoPrice.setText(String.valueOf(videoPrice));
            // mViewBinding.anchorDetailTvVoicePrice.setText(String.format(text, voicePrice));
        }
    }

    /**
     * 基本信息
     */
    private void setBaseInfo(@Nullable Anchor anchor) {
        String height = getString(R.string.height) + ":";
        String affective = getString(R.string.emotion) + ":";
        String job = getString(R.string.job) + ":";
        int startNum = 3;
        if (anchor != null) {
            if (TextUtils.isEmpty(anchor.getHeadUrl())) {
                if (anchor.getSex() == Sex.WOMAN.ordinal()) {
                    //ImageLoader.get().loadCropCircle(mViewBinding.anchorDetailIvAvatar, R.mipmap.icon_sex_select_woman);
                    ImageLoader.get().load(mViewBinding.anchorDetailHeaderImage, R.mipmap.icon_recommend_woman);
                } else {
                    //ImageLoader.get().loadCropCircle(mViewBinding.anchorDetailIvAvatar, R.mipmap.icon_sex_select_man);
                    ImageLoader.get().load(mViewBinding.anchorDetailHeaderImage, R.mipmap.icon_recommend_man);
                }
            } else {
                //ImageLoader.get().loadCropCircle(mViewBinding.anchorDetailIvAvatar, anchor.getHeadUrl());
                ImageLoader.get().load(mViewBinding.anchorDetailHeaderImage, anchor.getHeadUrl());
            }

            height += anchor.getHeight();
            job += anchor.getProfession();
            int affectiveStatus = anchor.getAffectiveState();
            if (affectiveStatus == 1) {
                affective += getResources().getString(R.string.single);
            } else if (affectiveStatus == 2) {
                affective += getResources().getString(R.string.in_love);
            } else {
                affective += getResources().getString(R.string.married);
            }
            int iconSexResId, bgSexResId;
            if (anchor.getSex() == Sex.WOMAN.ordinal()) {
                iconSexResId = R.mipmap.icon_sex_woman;
                bgSexResId = R.mipmap.sex_bg_woman;
                mViewBinding.anchorDetailTvInfoWx.setVisibility(View.VISIBLE);
            } else {
                iconSexResId = R.mipmap.icon_sex_man;
                bgSexResId = R.mipmap.sex_bg_man;
                mViewBinding.anchorDetailTvInfoWx.setVisibility(View.GONE);
            }
            mViewBinding.anchorDetailTvAge.setCompoundDrawablesWithIntrinsicBounds(
                    DisplayUtils.getDrawable(iconSexResId), null, null, null);
            mViewBinding.anchorDetailTvAge.setBackground(DisplayUtils.getDrawable(bgSexResId));

            mViewBinding.anchorDetailTvAge.setText(String.valueOf(anchor.getAge()));
            mViewBinding.anchorDetailTvUserName.setText(anchor.getNickname());
            mViewBinding.anchorDetailTvUserId.setText("ID:" + String.valueOf(anchor.getId()));
            if (anchor.getSignature() != null && !anchor.getSignature().isEmpty()) {
                mViewBinding.anchorDetailTvSign.setText(anchor.getSignature());
            } else {
                mViewBinding.anchorDetailTvSign.setVisibility(View.GONE);
            }
            mViewBinding.anchorDetailTvFans.setText(String.valueOf(anchor.getFansNum()) + "粉丝");
            if (anchor.isOnline() == OnlineStatus.ONLINE.getStatus()) {
                mViewBinding.anchorDetailIvOnlineStatus.setImageResource(R.mipmap.icon_anchor_status_on);
            } else {
                mViewBinding.anchorDetailIvOnlineStatus.setImageResource(R.mipmap.icon_anchor_status_off);
            }
            if (anchor.isFocus() == 1) {
                mViewBinding.anchorDetailTvAttention.setText(R.string.followed);
            } else {
                mViewBinding.anchorDetailTvAttention.setText(R.string.attention);
            }
            mViewBinding.anchorDetailTvAttention.setSelected(anchor.isFocus() == 1);

            if (anchor.getStarLevel() != null)
                startNum = anchor.getStarLevel();

            if (anchor.getVipType() == VipType.VIP_OF_S.getType()) {
                mViewBinding.anchorDetailIvCrown.setImageResource(R.mipmap.icon_svip_an_crown);
            } else if (anchor.getVipType() == VipType.VIP_OF_C.getType()) {
                mViewBinding.anchorDetailIvCrown.setImageResource(R.mipmap.icon_an_crown);
            } else {
                mViewBinding.anchorDetailIvCrown.setVisibility(View.GONE);
            }

       /*
        显示主播已认证
        if (anchor.getSex() == Sex.WOMAN.ordinal()) {
                Integer isAnchor = anchor.isAnchor();
                if (isAnchor != null) {
                    mViewBinding.anchorDetailIvAnchorAuthStatus.setVisibility(View.VISIBLE);
                    if (isAnchor == 1) {
                        mViewBinding.anchorDetailIvAnchorAuthStatus.setImageResource(R.mipmap.icon_anchor_authentication);
                    } else {
                        mViewBinding.anchorDetailIvAnchorAuthStatus.setImageResource(R.mipmap.icon_anchor_authentication);
                    }
                }
            } else {
                mViewBinding.anchorDetailIvAnchorAuthStatus.setVisibility(View.INVISIBLE);
            }*/
        }

        if (height != null) {
            String set1 = height.replace(".0", "");
            mViewBinding.anchorDetailTvInfoHeight.setText(set1 + "cm");
        }
        mViewBinding.anchorDetailTvInfoEmotion.setText(affective);
        mViewBinding.anchorDetailTvInfoJob.setText(job);
        mViewBinding.anchorDetailRsvStar.setStarNum(startNum);
    }

    /**
     * 相册
     */
    private void setAlbums(@Nullable Anchor anchor) {
        if (anchor != null) {
            if (!anchor.getPhotoUrls().isEmpty()) {
                int photoSize = anchor.getPhotoUrls().size();
                List<ImageBannerBean> data = new ArrayList<>();
                for (int i = 0; i < photoSize; i++) {
                    data.add(new ImageBannerBean(anchor.getPhotoUrls().get(i)));
                }
                bannerAdapter = new ImageBannerAdapter(getContext(), data);
                mViewBinding.banner.setAdapter(bannerAdapter, true);
                //bannerAdapter.setIncreaseCount(0);
                //是否允许自动轮播
                mViewBinding.banner.isAutoLoop(true);
                mViewBinding.banner.setStartPosition(0);
                mViewBinding.anchorAlbumNum.setText(mViewBinding.banner.getCurrentItem()+"/"+ photoSize);
                //开始轮播
                mViewBinding.banner.start();

                mViewBinding.banner.addOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPageSelected(int position) {
                        mViewBinding.anchorAlbumNum.setText((position + 1) + "/" + photoSize);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

                mViewBinding.banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        PreviewPicturesActivity.start(getActivity(), 0, position, (ArrayList<String>) anchor.getPhotoUrls());
                    }
                });

            } else {
                mViewBinding.anchorDetailHeaderImage.setVisibility(View.VISIBLE);
                mViewBinding.anchorAlbumNum.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 标签
     */
    private void setTags(@Nullable Anchor anchor) {
        if (anchor != null) {
            String tag = anchor.getTag();
            if (!TextUtils.isEmpty(tag)) {
                String[] tagArray = tag.split(",");
                ViewAnchorDetailTagBinding binding = ViewAnchorDetailTagBinding.inflate(getLayoutInflater());
                LinearLayout view = binding.getRoot();
                binding.viewTvTag.setVisibility(tagArray.length == 0 ? View.VISIBLE : View.GONE);
                for (String s : tagArray) {
                    view.addView(createTagTextView(Color.parseColor("#fff6f7"),
                            Color.parseColor("#fe7d99"), s));
                }
                mViewBinding.anchorInfoLlTag.addView(view);
            }
        }
    }

    /**
     * 评价
     */
    private void setEvaluations(@Nullable Anchor anchor) {
        if (anchor != null) {
            List<String> tags = anchor.getEvaruates();
            List<Integer> colorResIds = new ArrayList<>();
            colorResIds.add(Color.parseColor("#ff9d70"));
            colorResIds.add(Color.parseColor("#fe71fb"));
            colorResIds.add(Color.parseColor("#fe6c9d"));
            colorResIds.add(Color.parseColor("#97a1ff"));

            ViewAnchorDetailTagBinding binding = ViewAnchorDetailTagBinding.inflate(getLayoutInflater());
            LinearLayout view = binding.getRoot();
            if (tags == null || tags.isEmpty()) {
                binding.viewTvTag.setVisibility(View.VISIBLE);
            } else {
                if (tags.size() <= colorResIds.size()) {
                    binding.viewTvTag.setVisibility(View.GONE);
                    for (int i = 0; i < tags.size(); i++) {
                        view.addView(createTagTextView(colorResIds.get(i), Color.WHITE, tags.get(i)));
                    }
                }
            }
            mViewBinding.anchorInfoLlEvaluation.addView(view);
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    /**
     *
     */
    private TextView createTagTextView(@ColorInt int shapeColor,
                                       @ColorInt int textColor, String text) {
        SkyTextView tv = new SkyTextView(getActivity() != null ? getActivity() : ImLiveApp.get());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, (int) DisplayUtils.getDimension(R.dimen.tag_height));

        int padding = (int) DisplayUtils.getDimension(R.dimen.content_margin);
        params.setMargins(padding, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setPadding(padding, 0, padding, 0);

        tv.showBackgroundShape(true);
        tv.setBackgroundShape(SkyTextView.FILL_BACKGROUND_SHAPE);
        tv.setBackgroundRadius(R.dimen.text_margin);
        tv.setBackgroundStrokeWidth(R.dimen.divider);
        tv.setBackgroundShapeColor(shapeColor);

        tv.setTextColor(textColor);
        tv.setText(text);
        return tv;
    }
}