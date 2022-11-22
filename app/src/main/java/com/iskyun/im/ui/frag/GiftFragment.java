package com.iskyun.im.ui.frag;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.adapter.EaseChatExtendMenuIndicatorAdapter;
import com.hyphenate.easeui.widget.chatextend.HorizontalPageLayoutManager;
import com.hyphenate.easeui.widget.chatextend.PagingScrollHelper;
import com.iskyun.im.R;
import com.iskyun.im.adapter.GiftAdapter;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.GiveGiftResult;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.FragmentGiftBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.helper.PopupWindowHelper;
import com.iskyun.im.helper.SendMsgHelper;
import com.iskyun.im.ui.common.RechargeActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.event.DiamondChangeEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.GiftViewModel;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class GiftFragment extends BaseDialogFragment<FragmentGiftBinding> {
    public static final int NUM_ROWS = 2;
    public static final int NUM_COLUMNS = 4;

    private static final String USERID = "userId";
    private RecyclerView rvGift;
    private PagingScrollHelper helper;
    private GiftAdapter giftAdapter;
    private EaseChatExtendMenuIndicatorAdapter indicatorAdapter;
    private PopupWindowHelper popupHelper;
    private GiftViewModel giftViewModel;
    private GiftGiveCallback giftGiveCallback;

    private final List<Gift> gifts = new ArrayList<>();
    private final List<Gift> classicsGifts = new ArrayList<>();
    private final List<Gift> vipGifts = new ArrayList<>();
    private final List<TextView> textViews = new ArrayList<>();

    private int currentPosition = 0;
    private int totalDiamond;
    private int defaultNumber = 1;
    private int userId = 0;

    public GiftFragment(String conversationId) {
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(conversationId)) {
            conversationId = conversationId.replace(Constant.HX_ID, "");
        }
        try {
            args.putInt(USERID, Integer.parseInt(conversationId));
            setArguments(args);
        } catch (NumberFormatException e) {
            LogUtils.e(e.getMessage());
        }
    }


    @Override
    public FragmentGiftBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentGiftBinding.inflate(inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogParams(0);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USERID);
        }
        initGiftRv();
        initPageHelper();
        initIndicator();
        initGiftNumPop();
    }

    @Override
    public void initListener() {
        super.initListener();
        mViewBinding.giftTvClassics.setOnClickListener(this::onClick);
        mViewBinding.giftTvVip.setOnClickListener(this::onClick);
        mViewBinding.giftLlCount.setOnClickListener(this::onClick);
        mViewBinding.giftTvGive.setOnClickListener(this::onClick);
        mViewBinding.giftTvRecharge.setOnClickListener(this::onClick);
    }


    @Override
    public void initData() {
        super.initData();

        textViews.add(mViewBinding.giftTvClassics);
        textViews.add(mViewBinding.giftTvVip);

        mViewBinding.giftTvClassics.setSelected(true);
        giftViewModel = new ViewModelProvider(this).get(GiftViewModel.class);

        getGifts();
        setTotalPrice();
        setGiftNumber(defaultNumber);
        setMyDiamondNumber();


        giftViewModel.observerGiveGift().observe(this, giveGiftResult -> {
            SpManager.setDiamondBalance(totalDiamond);
            //ToastUtils.showToast(R.string.gift_send_success);
            dismiss();
            //发送礼物消息
            sendGiftMsg(giveGiftResult);
            LiveDataBus.get().with(Constant.DIAMOND_CHANGE).postValue(new DiamondChangeEvent(totalDiamond));
        });

        LiveDataBus.get().with(Constant.DIAMOND_CHANGE, DiamondChangeEvent.class).observe(this,
                diamondChangeEvent -> {
                    setMyDiamondContent();
                });
        setMyDiamondContent();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.gift_tv_classics) {
            switchGifts(mViewBinding.giftTvClassics, classicsGifts);
        } else if (v.getId() == R.id.gift_tv_vip) {
            switchGifts(mViewBinding.giftTvVip, vipGifts);
        } else if (v.getId() == R.id.gift_ll_count) {
            popupHelper.showAtLocation(mViewBinding.giftLlCount, Gravity.BOTTOM, DisplayUtils.dp2px(60),
                    DisplayUtils.dp2px(40));
        } else if (v.getId() == R.id.gift_tv_give) {
            giveGift();
        } else if (v.getId() == R.id.gift_tv_recharge) {
            ActivityUtils.launcher(getActivity(), RechargeActivity.class);
        }
    }

    private void sendGiftMsg(GiveGiftResult giveGiftResult) {
        Gift gift = giftAdapter.getSelectGift();
        if (gift != null) {
            gift.setGiftNum(defaultNumber);
            gift.setEarnings(giveGiftResult.getEarnings());
            if (giftGiveCallback != null)
                giftGiveCallback.giftGive(gift);
        }
    }

    private void giveGift() {
        Gift gift = giftAdapter.getSelectGift();
        if (gift == null) {
            return;
        }
        User user = SpManager.getInstance().getCurrentUser();

        if(gift.getGiftType() == 2 && user.getVipType() == 0){
            VipTipDialogFragment.show();
            return;
        }
        if (user.getUserDiamond() < gift.getGiftPrice()) {
            SendMsgHelper.getInstance().rechargeDiamond();
        }else {
            giftViewModel.giveGift(gift.getId(), userId);
        }
    }

    private void getGifts() {
        giftViewModel.getGifts().observe(this, giftType -> {
            classicsGifts.addAll(giftType.getCommonGift());
            switchGifts(mViewBinding.giftTvClassics, classicsGifts);
            vipGifts.addAll(giftType.getVipGift());
        });
    }

    private void initPageHelper() {
        helper = new PagingScrollHelper();
        helper.setUpRecycleView(rvGift);
        helper.updateLayoutManger();
        helper.scrollToPosition(0);
        mViewBinding.getRoot().setHorizontalFadingEdgeEnabled(true);
        helper.setOnPageChangeListener(this::setSelectedIndex);
    }

    private void initGiftRv() {
        rvGift = mViewBinding.giftRvGift;
        HorizontalPageLayoutManager manager = new HorizontalPageLayoutManager(NUM_ROWS, NUM_COLUMNS);
        manager.setItemHeight(DisplayUtils.dp2px(120));
        rvGift.setLayoutManager(manager);
        RecyclerView.ItemDecoration decoration = new GridItemDecoration(getActivity(),
                getResources().getDimensionPixelSize(R.dimen.gift_decoration_width), R.color.gift_color);
        rvGift.addItemDecoration(decoration);
        rvGift.setHasFixedSize(true);
        giftAdapter = new GiftAdapter();
        rvGift.setAdapter(giftAdapter);
        giftAdapter.setOnItemClickListener((adapter, view, position) -> {
            giftAdapter.selectPosition(position);
            setTotalPrice();
        });
    }

    /**
     * 导航
     */
    private void initIndicator() {
        RecyclerView rvIndicator = mViewBinding.giftRvIndicator;
        indicatorAdapter = new EaseChatExtendMenuIndicatorAdapter();
        rvIndicator.setAdapter(indicatorAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(DisplayUtils.getDrawable(com.hyphenate.easeui.R.drawable.ease_chat_extend_menu_indicator_divider));
        rvIndicator.addItemDecoration(itemDecoration);
        indicatorAdapter.setSelectedPosition(currentPosition);
    }

    /**
     * popupWindow
     */
    private void initGiftNumPop() {
        popupHelper = new PopupWindowHelper();
        String[] content = getResources().getStringArray(R.array.gift_number_content);
        int[] number = getResources().getIntArray(R.array.gift_number);
        if (content.length != number.length)
            return;
        for (int i = 0; i < content.length; i++) {
            popupHelper.addPopupItem(new PopupWindowHelper.PopupItemModel(number[i], content[i]));
        }
        popupHelper.create();
        popupHelper.addOnPopupItemClickListener(itemModel -> {
            defaultNumber = itemModel.getNumber();
            setTotalPrice();
            setGiftNumber(defaultNumber);
        });
    }

    private void setSelectedIndex(int index) {
        //设置选中的indicator
        currentPosition = index;
        indicatorAdapter.setSelectedPosition(currentPosition);
    }

    private void setMyDiamondContent() {
        int diamond = SpManager.getDiamondBalance();
        String myDiamond = getString(R.string.my_diamond) + ":";
        String content = myDiamond + diamond;
        SpannableString myDiamondContent = new SpannableString(content);
        myDiamondContent.setSpan(new ForegroundColorSpan(getResources()
                        .getColor(R.color.gift_text_color)), myDiamond.length(),
                content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mViewBinding.giftTvMyDiamond.setText(myDiamondContent);
    }


    /**
     * 设置总价显示的文本
     */
    private void setTotalPrice() {
        Gift gift = giftAdapter.getSelectGift();
        int price = 0;
        if (gift != null) {
            price = gift.getGiftPrice();
        }
        totalDiamond = price * defaultNumber;
        mViewBinding.giftTvTotal.setText(getTotalPriceContent(totalDiamond));
    }

    /**
     * 总价文本
     */
    private SpannableString getTotalPriceContent(int totalPrice) {
        String start = getString(R.string.total);
        String diamond = getString(R.string.diamond);
        String text = start + "%d" + diamond;
        SpannableString content = new SpannableString(String.format(text, totalPrice));
        content.setSpan(new ForegroundColorSpan(getResources()
                        .getColor(R.color.gift_text_color)), start.length(),
                String.valueOf(totalPrice).length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return content;
    }

    /**
     * 设置需要显示的indicator的个数
     */
    private void setPageCount(List<Gift> gifts) {
        if (gifts == null)
            return;
        indicatorAdapter.setPageCount((int) Math.ceil(gifts.size()
                * 1.0f / (NUM_COLUMNS * NUM_ROWS)));
    }

    /**
     * gift 数据
     */
    private void setGifts() {
        gifts.clear();
        if (mViewBinding.giftTvClassics.isSelected()) {
            gifts.addAll(classicsGifts);
        } else {
            gifts.addAll(vipGifts);
        }
        giftAdapter.setList(gifts);
    }

    /**
     * 切换经典礼物，vip礼物
     */
    private void switchGifts(TextView tvSelected, List<Gift> selectedGifts) {
        for (TextView tv : textViews) {
            tv.setSelected(tvSelected.getId() == tv.getId());
        }
        setPageCount(selectedGifts);
        setGifts();
        reset();
    }

    /**
     * 重置选中项
     */
    private void reset() {
        currentPosition = 0;
        indicatorAdapter.setSelectedPosition(0);
        helper.scrollToPosition(0);
        giftAdapter.selectPosition(GiftAdapter.DEFAULT_SELECT_INDEX);
    }

    private void setGiftNumber(int count) {
        mViewBinding.giftTvCount.setText(String.valueOf(count));
    }

    private void setMyDiamondNumber() {
        String content = DisplayUtils.getString(R.string.my_diamond);
        mViewBinding.giftTvMyDiamond.setText(content);
    }

    public void setGiftGiveCallback(GiftGiveCallback giftGiveCallback) {
        this.giftGiveCallback = giftGiveCallback;
    }

    public interface GiftGiveCallback {
        void giftGive(Gift gift);
    }

}
