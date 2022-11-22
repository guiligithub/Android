package com.iskyun.im.ui.common;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.MainAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.Complaint;
import com.iskyun.im.databinding.ActivityComplaintBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.common.viewmodel.ComplaintViewModel;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.ArrayList;
/**
 * 用户问题反馈
 * */
public class ComplaintActivity extends BaseActivity<ActivityComplaintBinding, ComplaintViewModel> {
    private MainAdapter adapter;
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private int maxNum = NUMBER_OF_SIX;
    private static final int NUMBER_OF_SIX = 4;
    private boolean off = false;
    private Complaint body=new Complaint();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public ComplaintViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(ComplaintViewModel.class);
    }

    @Override
    public ActivityComplaintBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityComplaintBinding.inflate(inflater);
    }

    /**
     * ComplaintActivity 启动
     * @param userId
     */
    public static void launcher(int userId) {
        Bundle args = new Bundle();
        args.putInt(Constant.ANCHOR_ID, userId);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(),args, ComplaintActivity.class);
    }

    @Override
    public void initBase() {
        if (getIntent().getExtras() != null) {
            int userId = getIntent().getExtras().getInt(Constant.ANCHOR_ID);
            body.setInformerId(String.valueOf(userId));
            mViewBinding.userInfoEdit.setEnabled(false);
        }

        RecyclerView.ItemDecoration decoration = new GridItemDecoration(this,
                getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.white);
        mViewBinding.complaintRecyclerview.addItemDecoration(decoration);
        mViewBinding.complaintRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter = new MainAdapter();
        adapter.setList(selectedPhotoList);
        adapter.setSelectMax(maxNum);
        mViewBinding.complaintRecyclerview.setAdapter(adapter);

        userInfoEdit();
        setOff();
        mViewBinding.tabIvBlock.setOnClickListener(view -> {
            setOff();
            off = !off;
        });
        mViewBinding.userInfoSubmit.setOnClickListener(this::onClick);
        mViewBinding.setComplaint(body);


        mViewBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                if (getResources().getString(R.string.spread_pornography).equals(radbtn.getText().toString())){
                    body.setReportReasons(1);
                }else if (getResources().getString(R.string.advertisement).equals(radbtn.getText().toString())){
                    body.setReportReasons(2);
                }else if (getResources().getString(R.string.swindle).equals(radbtn.getText().toString())){
                    body.setReportReasons(3);
                }else if (getResources().getString(R.string.other).equals(radbtn.getText().toString())){
                    body.setReportReasons(4);
                }else {
                    body.setReportReasons(5);
                }
            }
        });

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ArrayList<String> list=new ArrayList<>();
                for(int i = 0;i < selectedPhotoList.size(); i++){
                    list.add(selectedPhotoList.get(i).path);
                }
                PreviewPicturesActivity.start(ComplaintActivity.this, 0, position, list);
            }

            @Override
            public void onPictureSelect() {
                if (PermissionUtil.checkAndRequestPermissionsInActivity(ImLiveApp.get().getTopActivity(),
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    EasyPhotos.createAlbum(ComplaintActivity.this, true, false, GlideEngine.getInstance())
                            .setFileProviderAuthority(Constant.AUTHORITY)
                            .setCount(NUMBER_OF_SIX)
                            .setSelectedPhotos(selectedPhotoList)
                            .start(UploadType.ALBUM.getValue());
                }
            }
        });

        mViewModel.observerUpdate().observe(this,complaint -> {
            ToastUtils.showToast(R.string.complaint_success);
            LiveDataBus.get().with(Constant.COMPLAINT_SUCCESS).postValue(complaint);
            finish();
        });

    }

    private void setOff() {
        if (off) {
            mViewBinding.tabIvBlock.setImageResource(R.mipmap.icon_switch_on);
        } else {
            mViewBinding.tabIvBlock.setImageResource(R.mipmap.icon_switch_off);
        }
    }

    private void userInfoEdit() {
        mViewBinding.userInfoTvCount.setText(mViewBinding.reportInfoEdit.getText().toString().length()+"/50");
        mViewBinding.reportInfoEdit.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int UserInfoEditStart;
            private int UserInfoEditEnd;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                temp=charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                UserInfoEditStart=mViewBinding.reportInfoEdit.getSelectionStart();
                UserInfoEditEnd=mViewBinding.reportInfoEdit.getSelectionEnd();
                mViewBinding.userInfoTvCount.setText(temp.length()+"/50");
                if(temp.length()>50){
                    editable.delete(UserInfoEditStart-1,UserInfoEditEnd);
                    int tempSelection=UserInfoEditStart;
                    mViewBinding.reportInfoEdit.setText(editable);
                    mViewBinding.reportInfoEdit.setSelection(tempSelection);
                }
            }
        });
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.user_info_submit:
                    release();
                break;
        }
    }

    /**
     * 提交
     */
    private void release() {
        body.setUserId(user.getId());
        LogUtils.e(GsonUtils.toJson(body));
        mViewModel.publishDynamic(selectedPhotoList, body);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (requestCode == UploadType.ALBUM.getValue()) {
            selectedPhotoList.clear();
            selectedPhotoList.addAll(resultPhotos);
            adapter.setSelectMax(maxNum);
            adapter.notifyItemRangeChanged(0, selectedPhotoList.size());
            mViewBinding.screenshotCount.setText(selectedPhotoList.size()+"/4");
        }
    }

    @Override
    public int getTitleText() {
        return R.string.feedback;
    }
}