package com.iskyun.im.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.github.gzuliyujiang.dialog.DialogConfig;
import com.github.gzuliyujiang.wheelpicker.OptionPicker;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.req.AnchorPriceSetting;
import com.iskyun.im.databinding.ActivityPriceSettingBinding;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.iskyun.im.utils.manager.SpManager;

import java.util.ArrayList;
import java.util.List;

public class PriceSettingActivity extends BaseActivity<ActivityPriceSettingBinding, LoginViewModel> {


    private AnchorPriceSetting anchorPriceSetting;
    private final int[] videoPrice = {0, 30, 35, 40, 45, 50, 55, 60, 65, 70};
    private final int[] voicePrice = {0, 10, 15, 20, 25, 30, 35, 40};
    private final int[] textPrice = {0, 2};
    private final int[] wechatPrice = {0, 1888, 2888};

    @Override
    public LoginViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityPriceSettingBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityPriceSettingBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mViewBinding.priceSettingChat.setOnClickListener(this::onClick);
        mViewBinding.priceSettingVideo.setOnClickListener(this::onClick);
        mViewBinding.priceSettingVoice.setOnClickListener(this::onClick);
        mViewBinding.priceSettingLookWx.setOnClickListener(this::onClick);
        anchorPriceSetting = SpManager.getInstance().getPriceSetting();
        setPriceContent();
    }

    @Override
    public void finish() {
        SpManager.getInstance().setPriceSetting(anchorPriceSetting);
        setResult(Activity.RESULT_OK, new Intent());
        super.finish();
    }

    private void setPriceContent() {
        mViewBinding.priceSettingVideo.setContent(getPriceContent(anchorPriceSetting.getVideoMinute(), 1));
        mViewBinding.priceSettingVoice.setContent(getPriceContent(anchorPriceSetting.getVoiceMinute(), 2));
        mViewBinding.priceSettingChat.setContent(getPriceContent(anchorPriceSetting.getTextPrice(), 3));
        mViewBinding.priceSettingLookWx.setContent(getPriceContent(anchorPriceSetting.getUnlockWechat(), 4));
    }

    private String getPriceContent(Integer price, int type) {
        String videoContent = null;
        if (price == null) {
            videoContent = getString(R.string.unselect);
        } else {
            if (type == 1) {
                videoContent = price + getString(R.string.video_minute);
            } else if (type == 2) {
                videoContent = price + getString(R.string.video_minute);
            } else if (type == 3) {
                videoContent = price + getString(R.string.text_minute);
            } else if (type == 4) {
                videoContent = price + getString(R.string.diamond);
            }
        }
        return videoContent;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.price_setting_video) {
            showPicker(1, videoPrice);
        } else if (v.getId() == R.id.price_setting_voice) {
            showPicker(2, voicePrice);
        } else if (v.getId() == R.id.price_setting_chat) {
            showPicker(3, textPrice);
        } else if (v.getId() == R.id.price_setting_look_wx) {
            showPicker(4, wechatPrice);
        }
    }

    private void showPicker(int type, int[] arrayPrice) {
        OptionPicker picker = new OptionPicker(this);
        picker.setData(getData(type, arrayPrice));
        picker.setDefaultPosition(getDefaultPosition(type, arrayPrice));
        picker.setOnOptionPickedListener((position, item) -> {
            setPriceBody(type, position);
            setPriceContent();
        });
        DialogConfig.setDialogStyle(0);
//        picker.getCancelView().setVisibility(View.GONE);
//        picker.getOkView().setVisibility(View.GONE);
        picker.setTitle(getPickerTitle(type));
        picker.show();
    }

    private int getDefaultPosition(int type, int[] arrayPrice) {
        int position = 0;
        int price;
        if (type == 1 && anchorPriceSetting.getVideoMinute() != null) {
            price = anchorPriceSetting.getVideoMinute();
        } else if (type == 2 && anchorPriceSetting.getVoiceMinute() != null) {
            price = anchorPriceSetting.getVoiceMinute();
        } else if (type == 3 && anchorPriceSetting.getTextPrice() != null) {
            price = anchorPriceSetting.getTextPrice();
        } else if (type == 4 && anchorPriceSetting.getUnlockWechat() != null) {
            price = anchorPriceSetting.getUnlockWechat();
        } else {
            price = 0;
        }
        for (int i = 0; i < arrayPrice.length; i++) {
            if (price == arrayPrice[i]) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void setPriceBody(int type, int position) {
        if (type == 1) {
            anchorPriceSetting.setVideoMinute(videoPrice[position]);
        } else if (type == 2) {
            anchorPriceSetting.setVoiceMinute(voicePrice[position]);
        } else if (type == 3) {
            anchorPriceSetting.setTextPrice(textPrice[position]);
        } else if (type == 4) {
            anchorPriceSetting.setUnlockWechat(wechatPrice[position]);
        }
    }

    private List<String> getData(int type, int[] arrayPrice) {
        List<String> lists = new ArrayList<>();
        String content = "";
        if (type == 1) {
            content = getString(R.string.video_minute);
        } else if (type == 2) {
            content = getString(R.string.video_minute);
        } else if (type == 3) {
            content = getString(R.string.text_minute);
        } else if (type == 4) {
            content = getString(R.string.diamond);
        }
        for (int index : arrayPrice) {
            String indexContent = index + content;
            lists.add(indexContent);
        }
        return lists;
    }

    private String getPickerTitle(int type) {
        String pickerTitle = "";
        if (type == 1) {
            pickerTitle = getString(R.string.video_call_price);
        } else if (type == 2) {
            pickerTitle = getString(R.string.voice_call_price);
        } else if (type == 3) {
            pickerTitle = getString(R.string.msg_price);
        } else if (type == 4) {
            pickerTitle = getString(R.string.look_wx_price);
        }
        return pickerTitle;
    }


    @Override
    public int getTitleText() {
        return R.string.price_setting;
    }
}