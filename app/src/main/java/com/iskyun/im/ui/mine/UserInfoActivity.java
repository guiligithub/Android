package com.iskyun.im.ui.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.lifecycle.ViewModelProvider;

import com.github.gzuliyujiang.dialog.DialogConfig;
import com.github.gzuliyujiang.dialog.DialogStyle;
import com.github.gzuliyujiang.wheelpicker.AddressPicker;
import com.github.gzuliyujiang.wheelpicker.NumberPicker;
import com.github.gzuliyujiang.wheelpicker.OptionPicker;
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressPickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnNumberPickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnOptionPickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity;
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity;
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.databinding.ActivityUserInfoBinding;
import com.iskyun.im.databinding.ViewAnchorDetailTagBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.UserUtils;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.FileUploadViewModel;
import com.iskyun.im.viewmodel.UpdateUserViewModel;
import com.iskyun.im.widget.SkyTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoActivity extends BaseActivity<ActivityUserInfoBinding, UpdateUserViewModel> {
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private MediaPlayer mPlayer = null;
    private FileUploadViewModel uploadViewModel;
    private String tags;

    @Override
    public UpdateUserViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(UpdateUserViewModel.class);
    }

    @Override
    public ActivityUserInfoBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityUserInfoBinding.inflate(inflater);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initBase() {

        uploadViewModel = new ViewModelProvider(this).get(FileUploadViewModel.class);
        user = SpManager.getInstance().getCurrentUser();
        LogUtils.e(GsonUtils.toJson(user));
        if (user != null) {
            if (user.getSex() == Sex.MAN.ordinal()) {
                mViewBinding.userInfoViewSex.setContent(getResources().getString(R.string.man));
                mViewBinding.userInfoViewWechat.setVisibility(View.GONE);
            } else {
                mViewBinding.userInfoViewSex.setContent(getResources().getString(R.string.woman));
            }
            mViewBinding.userInfoViewNick.setContent(user.getNickname());
            mViewBinding.userInfoViewAge.setContent(Integer.toString(user.getAge()));

            UserUtils.setHeaderUrl(mViewBinding.userInfoIvAvatar);

            if (!TextUtils.isEmpty(user.getProfession()))
                mViewBinding.userInfoViewJob.setContent(user.getProfession());

            if (!TextUtils.isEmpty(user.getTag()))
                setEvaluations(user.getTag());

            if (user.getHeight() != 0.0) {
                DecimalFormat format = new DecimalFormat("#");
                mViewBinding.userInfoViewHeight.setContent(format.format(user.getHeight()));
            }

            if (!TextUtils.isEmpty(user.getWxNumber()))
                mViewBinding.userInfoViewWechat.setContent(user.getWxNumber());

            if (!TextUtils.isEmpty(user.getCity()))
                mViewBinding.userInfoViewCity.setContent(user.getCity());

            if (!TextUtils.isEmpty(user.getSignature()))
                mViewBinding.userInfoEdit.setText(user.getSignature());

            if (user.getAffectiveState() == 1) {
                mViewBinding.userInfoViewEmotion.setContent(getResources().getString(R.string.single));
            } else if (user.getAffectiveState() == 2) {
                mViewBinding.userInfoViewEmotion.setContent(getResources().getString(R.string.in_love));
            } else {
                mViewBinding.userInfoViewEmotion.setContent(getResources().getString(R.string.married));
            }

        }
        mViewBinding.userInfoIvAvatar.setOnClickListener(this::onClick);
        mViewBinding.userInfoSave.setOnClickListener(this::onClick);
        userInfoEdit();

        mViewModel.observerUpdate().observe(this, user -> {
            ToastUtils.showToast(R.string.update_success);
            LiveDataBus.get().with(Constant.UPDATE_USER_SUCCESS).postValue(user);
            finish();
        });

        uploadViewModel.observerPaths().observe(this, strings -> {
            if (!strings.isEmpty()) {
                user.setHeadUrl(strings.get(0));
            }
        });

        mViewBinding.userInfoNsv.setOnTouchListener((v, event) -> {
            onTouchHideSoft();
            return false;
        });

        mViewBinding.userInfoViewNick.getContentEditText().setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(10)
        });
    }

    /*输入框监听*/
    private void userInfoEdit() {
        mViewBinding.userInfoTvCount.setText(mViewBinding.userInfoEdit.getText().toString().length() + "/20");
        mViewBinding.userInfoEdit.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int UserInfoEditStart;
            private int UserInfoEditEnd;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                UserInfoEditStart = mViewBinding.userInfoEdit.getSelectionStart();
                UserInfoEditEnd = mViewBinding.userInfoEdit.getSelectionEnd();
                mViewBinding.userInfoTvCount.setText(temp.length() + "/20");
                if (temp.length() > 20) {
                    editable.delete(UserInfoEditStart - 1, UserInfoEditEnd);
                    int tempSelection = UserInfoEditStart;
                    mViewBinding.userInfoEdit.setText(editable);
                    mViewBinding.userInfoEdit.setSelection(tempSelection);
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        super.onClick(view);
        MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_USERINFO_ALBUM);
        DialogConfig.setDialogStyle(DialogStyle.Three);
        switch (view.getId()) {
            case R.id.user_info_view_nick:
                mViewBinding.userInfoViewNick.updateEditContent(R.mipmap.icon_close, content -> user.setNickname(content));
                break;
            case R.id.user_info_view_wechat:
                mViewBinding.userInfoViewWechat.updateEditContent(R.mipmap.icon_close, content -> user.setWxNumber(content));
                break;
            case R.id.user_info_view_age:
                NumberPicker agepicker = new NumberPicker(this);
                agepicker.setOnNumberPickedListener(new OnNumberPickedListener() {
                    @Override
                    public void onNumberPicked(int position, Number item) {
                        user.setAge(agepicker.getWheelView().getItem(position));
                        mViewBinding.userInfoViewAge.setContent(agepicker.getWheelView().formatItem(position));
                    }
                });
                agepicker.setRange(16, 60, 1);
                agepicker.setDefaultValue(16);
                agepicker.setTitle(getResources().getString(R.string.age_selection));
                agepicker.show();
                break;
            case R.id.user_info_view_job:
                OptionPicker pickers = new OptionPicker(this);
                pickers.setData((Object[]) getResources().getStringArray(R.array.occupation));
                pickers.setOnOptionPickedListener(new OnOptionPickedListener() {
                    @Override
                    public void onOptionPicked(int position, Object item) {
                        user.setProfession(pickers.getWheelView().getItem(position));
                        mViewBinding.userInfoViewJob.setContent(pickers.getWheelView().formatItem(position));
                    }
                });
                pickers.setTitle(getResources().getString(R.string.job_selection));
                pickers.show();
                break;
            case R.id.user_info_view_height:
                NumberPicker picker2 = new NumberPicker(this);
                picker2.setOnNumberPickedListener((position, item) -> {
                    user.setHeight(picker2.getWheelView().getItem(position));
                    mViewBinding.userInfoViewHeight.setContent(picker2.getWheelView().formatItem(position));
                });
                picker2.setRange(140, 200, 1);
                picker2.setDefaultValue(172);
                picker2.setTitle(getResources().getString(R.string.height_selection));
                picker2.show();
                break;
            case R.id.user_info_iv_avatar:
                fragment.setOnItemClickListener(this::onItemClick);
                fragment.show(getSupportFragmentManager(), "album");
                break;
            case R.id.user_info_view_tag:
                Intent intent = new Intent();
                intent.setClass(this, SelectLabelActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.user_info_view_emotion:
                OptionPicker emotionpicker = new OptionPicker(this);
                emotionpicker.setData((Object[]) getResources().getStringArray(R.array.emotional_state));
                emotionpicker.setOnOptionPickedListener(new OnOptionPickedListener() {
                    @Override
                    public void onOptionPicked(int position, Object item) {
                        String content = emotionpicker.getWheelView().formatItem(position);
                        if (getResources().getString(R.string.single).equals(content)) {
                            user.setAffectiveState(1);
                        } else if (getResources().getString(R.string.in_love).equals(content)) {
                            user.setAffectiveState(2);
                        } else {
                            user.setAffectiveState(3);
                        }
                        mViewBinding.userInfoViewEmotion.setContent(content);
                    }
                });
                emotionpicker.setTitle(getResources().getString(R.string.emotional_state));
                emotionpicker.show();
                break;

            case R.id.user_info_view_city:
                AddressPicker citypicker = new AddressPicker(this);
                citypicker.setTitle(getResources().getString(R.string.province_and_city_selection));
                citypicker.setAddressMode(AddressMode.PROVINCE_CITY);
                citypicker.setDefaultValue("520000", "520100", "");
                citypicker.setOnAddressPickedListener(new OnAddressPickedListener() {
                    @Override
                    public void onAddressPicked(ProvinceEntity province, CityEntity city, CountyEntity county) {
                        String cityContent = String.format("%s%s%s",
                                citypicker.getFirstWheelView().formatItem(province),
                                citypicker.getSecondWheelView().formatItem(city),
                                citypicker.getThirdWheelView().formatItem(county));
                        user.setCity(cityContent);
                        mViewBinding.userInfoViewCity.setContent(cityContent);
                    }
                });
                citypicker.setTitle(getResources().getString(R.string.select_location));
                citypicker.show();
                break;
            case R.id.user_info_save:
                hideSoftInput();
                if (TextUtils.isEmpty(user.getNickname())) {
                    ToastUtils.showToast(R.string.perfect_information);
                    return;
                }
                user.setSignature(mViewBinding.userInfoEdit.getText().toString().trim());
                mViewModel.update(user);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle bundle = data.getExtras();
            tags = bundle.getString("tags");
            if (tags != null && !tags.isEmpty()) {
                setEvaluations(tags);
            }
        }
        if (resultCode != RESULT_OK || data == null)
            return;
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (requestCode == UploadType.ALBUM.getValue()) {
            if (resultPhotos != null && !resultPhotos.isEmpty()) {
                doActivityResult(resultPhotos.get(0));
            }
        } else if (requestCode == UploadType.CAMERA_PIC.getValue()) {
            if (resultPhotos != null && !resultPhotos.isEmpty()) {
                doActivityResult(resultPhotos.get(0));
            }
        }
    }

    private void doActivityResult(Photo photo) {
        if (photo.path != null) {
            ImageLoader.get().loadCropCircle(mViewBinding.userInfoIvAvatar, photo.path);
            //user.setHeadUrl(photo.path);
            List<String> list = new ArrayList<>();
            list.add(photo.path);
            uploadViewModel.uploads(list);
        }
    }

    private TextView createTagTextView(@ColorInt int shapeColor,
                                       @ColorInt int textColor, String text) {
        SkyTextView tv = new SkyTextView(this != null ? this : ImLiveApp.get());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, (int) DisplayUtils.getDimension(R.dimen.tag_height));
        int padding = (int) DisplayUtils.getDimension(R.dimen.content_margin);
        int margin = (int) DisplayUtils.getDimension(R.dimen.text_margin);
        params.gravity = Gravity.CENTER;
        params.setMargins(margin, 0, 0, 0);
        tv.setLayoutParams(params);
        tv.setPadding(padding, 5, padding, 5);
        tv.setGravity(Gravity.CENTER);//文字的位置
        tv.showBackgroundShape(true);
        tv.setBackgroundShape(SkyTextView.FILL_BACKGROUND_SHAPE);
        tv.setBackgroundRadius(R.dimen.text_margin);
        tv.setBackgroundStrokeWidth(R.dimen.divider);
        tv.setBackgroundShapeColor(shapeColor);
        tv.setTextColor(Color.WHITE);
        tv.setText(text);
        return tv;
    }

    private LinearLayout rootTagView;
    private ViewAnchorDetailTagBinding tagBinding;

    private void setEvaluations(String tab) {
        String set1 = tab.replace("[", "");
        String set = set1.replace("]", "");
        user.setTag(set);
        String[] strs = set.split(",");//根据，切分字符串
        List<String> tags = new ArrayList<>(Arrays.asList(strs));
        List<Integer> colorResIds = new ArrayList<>();
        colorResIds.add(Color.parseColor("#ff9d70"));
        colorResIds.add(Color.parseColor("#fe71fb"));
        colorResIds.add(Color.parseColor("#fe6c9d"));
        colorResIds.add(Color.parseColor("#97a1ff"));
        if (tagBinding == null) {
            tagBinding = ViewAnchorDetailTagBinding.inflate(getLayoutInflater());
            rootTagView = tagBinding.getRoot();
        } else {
            rootTagView.removeAllViews();
            mViewBinding.userInfoViewTag.removeView(rootTagView);
        }
        tagBinding.viewTvTag.setVisibility(tab.isEmpty() ? View.VISIBLE : View.GONE);
        for (int i = 0; i < tags.size(); i++) {
            rootTagView.addView(createTagTextView(colorResIds.get(i), colorResIds.get(i), tags.get(i)));
        }
        mViewBinding.userInfoViewTag.setContent("");
        mViewBinding.userInfoViewTag.addView(rootTagView);
    }

    private void onItemClick(MoreActionFragment.MoreType type) {
        switch (type) {
            case ALBUM:
                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .setCount(1)
                        .setSelectedPhotos(selectedPhotoList)
                        .start(UploadType.ALBUM.getValue());
                break;
            case PICTURE:
                EasyPhotos.createCamera(this, false)
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .start(UploadType.CAMERA_PIC.getValue());
                break;
            default:
                break;
        }
    }

    @Override
    public int getTitleText() {
        return R.string.modify_data;
    }

    /**
     * onTouch 隐藏软键盘，清除焦点
     */
    private void onTouchHideSoft() {
        View view = getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null && view != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mViewBinding.userInfoViewNick.clearFocus();
        mViewBinding.userInfoViewWechat.clearFocus();
    }
}