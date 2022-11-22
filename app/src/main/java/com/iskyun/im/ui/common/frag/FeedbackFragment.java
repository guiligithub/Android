package com.iskyun.im.ui.common.frag;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.MainAdapter;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.Suggest;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.FragmentComplaintProposalBinding;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.common.PreviewPicturesActivity;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.ui.common.viewmodel.FeedbackViewModel;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.ArrayList;

public class FeedbackFragment extends BaseFragment<FragmentComplaintProposalBinding, FeedbackViewModel> implements View.OnClickListener {
    private MainAdapter adapter;
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private int maxNum = NUMBER_OF_SIX;
    private static final int NUMBER_OF_SIX = 4;
    private boolean off = false;
    private Suggest body=new Suggest();
    private User user;

    @Override
    public FeedbackViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(FeedbackViewModel.class);
    }

    @Override
    public FragmentComplaintProposalBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentComplaintProposalBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        user= SpManager.getInstance().getCurrentUser();
        RecyclerView.ItemDecoration decoration = new GridItemDecoration(getActivity(),
                getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.white);
        mViewBinding.complaintRecyclerview.addItemDecoration(decoration);
        mViewBinding.complaintRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter = new MainAdapter();
        adapter.setList(selectedPhotoList);
        adapter.setSelectMax(maxNum);
        mViewBinding.complaintRecyclerview.setAdapter(adapter);

        mViewBinding.userInfoSubmit.setOnClickListener(this::onClick);
        //mViewBinding.setComplaint(body);
        userInfoEdit();
        body.setIdeaText(mViewBinding.reportInfoEdit.getText().toString());

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ArrayList<String> list=new ArrayList<>();
                for(int i = 0;i < selectedPhotoList.size(); i++){
                    list.add(selectedPhotoList.get(i).path);
                }
                PreviewPicturesActivity.start(getActivity(), 0, position, list);
            }

            @Override
            public void onPictureSelect() {
                if (PermissionUtil.checkAndRequestPermissionsInActivity(ImLiveApp.get().getTopActivity(),
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    EasyPhotos.createAlbum(getActivity(), true, false, GlideEngine.getInstance())
                            .setFileProviderAuthority(Constant.AUTHORITY)
                            .setCount(NUMBER_OF_SIX)
                            .setSelectedPhotos(selectedPhotoList)
                            .start(UploadType.ALBUM.getValue());
                }
            }
        });
    }

    private void userInfoEdit() {
        mViewBinding.userInfoTvCount.setText(mViewBinding.reportInfoEdit.getText().toString().length()+"/200");
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
                mViewBinding.userInfoTvCount.setText(temp.length()+"/200");
                if(temp.length()>200){
                    editable.delete(UserInfoEditStart-1,UserInfoEditEnd);
                    int tempSelection=UserInfoEditStart;
                    mViewBinding.reportInfoEdit.setText(editable);
                    mViewBinding.reportInfoEdit.setSelection(tempSelection);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onClick(View view) {
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
       // body.setUserId(user.getId());
        LogUtils.e(GsonUtils.toJson(body));
        mViewModel.publishSuggest(selectedPhotoList, body);
    }

    @Override
    public int getTitleText() {
        return 0;
    }

}
