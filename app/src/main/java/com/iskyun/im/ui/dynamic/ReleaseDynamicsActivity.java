package com.iskyun.im.ui.dynamic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.ViewModelProvider;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gyf.immersionbar.ImmersionBar;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.MainAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.req.DynamicBody;
import com.iskyun.im.data.req.DynamicLookType;
import com.iskyun.im.databinding.ActivityReleaseDynamicsBinding;
import com.iskyun.im.ui.common.PreviewPicturesActivity;
import com.iskyun.im.ui.common.VideoPlayActivity;
import com.iskyun.im.ui.dynamic.frag.DynamicTabFragment;
import com.iskyun.im.ui.dynamic.viewmodel.DynamicViewModel;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.ui.mine.MineDynamicActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.utils.manager.PermissionsManager;

import java.util.ArrayList;

/**
 * 发布动态
 */
public class ReleaseDynamicsActivity extends BaseActivity<ActivityReleaseDynamicsBinding, DynamicViewModel> {

    public static final int DYNAMIC_REQ = 0x100;
    private static final int NUMBER_OF_SIX = 4;
    private static final int NUMBER_OF_ONE = 1;
    private LocationClient mLocationClient = null;

    /**
     * 选择的图片集
     */
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private MainAdapter adapter;
    private int maxNum = NUMBER_OF_SIX;
    private final DynamicBody body = new DynamicBody();
    private int fromType;

    @Override
    public DynamicViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(DynamicViewModel.class);
    }

    @Override
    public ActivityReleaseDynamicsBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityReleaseDynamicsBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        if(getIntent() != null && getIntent().getExtras() != null)
            fromType = getIntent().getExtras().getInt(DynamicTabFragment.DYNAMIC);
        ImmersionBar.with(this).keyboardEnable(false,
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .statusBarDarkFont(true).init();
        initView();
        getLocation();
    }

    @Override
    public int getTitleText() {
        return R.string.title_release_dynamic;
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
       /* RecyclerView.ItemDecoration decoration = new GridItemDecoration(this,
                getResources().getDimensionPixelSize(R.dimen.gift_decoration_width), R.color.white);
        mViewBinding.dynamicRv.addItemDecoration(decoration);*/
        mViewBinding.dynamicRv.setOverScrollMode(View.OVER_SCROLL_NEVER);

        adapter = new MainAdapter();
        adapter.setList(selectedPhotoList);
        adapter.setSelectMax(maxNum);
        mViewBinding.dynamicRv.setAdapter(adapter);

        mTvOther.setOnClickListener(this::onClick);
        mTvOther.setText(R.string.release_dynamic);
        mTvOther.setTextColor(R.color.colorAccent);
        mTvOther.setVisibility(View.VISIBLE);

        mViewBinding.dynamicTvLookContent.setOnClickListener(this::onClick);
        mViewBinding.dynamicLlLocation.setOnClickListener(this::onClick);
        mViewBinding.dynamicIvDeleteLocation.setOnClickListener(this::onClick);
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Photo photo = selectedPhotoList.get(position);
                if (photo.type.startsWith("video/")) {
                    VideoPlayActivity.launcherVideoPlay(ImLiveApp.get().getTopActivity(), photo.path);
                } else {
                    //预览图片
                    ArrayList<String> list=new ArrayList<>();
                    for(int i = 0;i < selectedPhotoList.size(); i++){
                            list.add(selectedPhotoList.get(i).path);
                    }
                    PreviewPicturesActivity.start(ReleaseDynamicsActivity.this, 0, position, list);
                }
            }

            @Override
            public void onPictureSelect() {
                if (PermissionUtil.checkAndRequestPermissionsInActivity(ImLiveApp.get().getTopActivity(),
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_DYNAMIC_ALBUM);
                    fragment.setOnItemClickListener(ReleaseDynamicsActivity.this::onItemClick);
                    fragment.show(getSupportFragmentManager(), "album");
                }
            }
        });

        mViewModel.observerPublish().observe(this, s -> {
            ToastUtils.showToast(R.string.release_success);
            if(fromType == DynamicTabFragment.MINE_DYNAMIC){
                setResult(RESULT_OK);
            }else {
                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), MineDynamicActivity.class);
            }
            finish();
        });

        body.setAddress("");
        body.setLookType(DynamicLookType.OPEN.getLookType());
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.tl_tv_other) {
            release();
        } else if (view.getId() == R.id.dynamic_tv_look_content) {
            MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_DYNAMIC_RELEASE);
            fragment.setOnItemClickListener(ReleaseDynamicsActivity.this::onItemClick);
            fragment.show(getSupportFragmentManager(), "open");
        } else if (view.getId() == R.id.dynamic_ll_location) {
            getLocation();
        } else if (view.getId() == R.id.dynamic_iv_delete_location) {
            mViewBinding.dynamicTvLocation.setText("");
            mViewBinding.dynamicIvDeleteLocation.setVisibility(View.GONE);
        }
    }

    /**
     * @param type
     */
    private void onItemClick(MoreActionFragment.MoreType type) {
        switch (type) {
            case ALBUM:
                maxNum = NUMBER_OF_SIX;
                EasyPhotos.createAlbum(ReleaseDynamicsActivity.this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .setCount(maxNum)
                        .setSelectedPhotos(selectedPhotoList)
                        .start(UploadType.ALBUM.getValue());
                break;
            case VIDEO:
                if (!selectedPhotoList.isEmpty()) {
                    Photo photo = selectedPhotoList.get(0);
                    if (photo.type.startsWith("image/")) {
                        ToastUtils.showToast(R.string.select_tips);
                        return;
                    }
                }
                maxNum = NUMBER_OF_ONE;
                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .setCount(maxNum)
                        .filter(Type.VIDEO)
                        .start(UploadType.VIDEO.getValue());
                break;
            case PICTURE:
                EasyPhotos.createCamera(this, false)
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .start(UploadType.CAMERA_PIC.getValue());
                break;
            case VIEW_PERMISSION:
                mViewBinding.dynamicTvLookContent.setText(type.getContent());
                body.setLookType(DynamicLookType.OPEN.getLookType());
                break;
            case MY_ATTENTION:
                mViewBinding.dynamicTvLookContent.setText(type.getContent());
                body.setLookType(DynamicLookType.ATTENTION.getLookType());
                break;
            case MY_FANS:
                mViewBinding.dynamicTvLookContent.setText(type.getContent());
                body.setLookType(DynamicLookType.FANS.getLookType());
                break;
            case PRIVACY:
                mViewBinding.dynamicTvLookContent.setText(type.getContent());
                body.setLookType(DynamicLookType.PRIVACY.getLookType());
                break;
            default:
                break;
        }
    }

    private void initLocationOption() {
        LocationClient mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                body.setAddress(bdLocation.getCity());
                mViewBinding.dynamicTvLocation.setText(body.getAddress());
                mViewBinding.dynamicIvDeleteLocation.setVisibility(View.VISIBLE);
            }
        });
        mLocationClient.start();
    }

    /**
     * 位置信息
     */
    private void getLocation() {
        PermissionsManager.getInstance().permissions(this, this::initLocationOption,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * 发布
     */
    private void release() {
        body.setUserId(user.getId());
        body.setTextDetails(mViewBinding.dynamicEtContent.getText().toString().trim());
        mViewModel.publishDynamic(selectedPhotoList, body);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (requestCode == UploadType.ALBUM.getValue()) {
            onActivityResult(resultPhotos);
        } else if (requestCode == UploadType.VIDEO.getValue()) {
            onActivityResultForVideo(resultPhotos);
        } else if (requestCode == UploadType.CAMERA_PIC.getValue()) {
            onActivityResult(resultPhotos);
        }
    }

    private void onActivityResultForVideo(ArrayList<Photo> photos) {
        if (photos != null && !photos.isEmpty()) {
            Photo photo = photos.get(0);
            if (photo.duration > Constant.VIDEO_MAX_DURATION) {
                ToastUtils.showToast(String.format(DisplayUtils.getString(R.string.video_too_long_tips),
                        Constant.VIDEO_DURATION));
                return;
            }
            onActivityResult(photos);
        }
    }

    private void onActivityResult(ArrayList<Photo> photos) {
        selectedPhotoList.clear();
        selectedPhotoList.addAll(photos);
        adapter.setSelectMax(maxNum);
        adapter.notifyItemRangeChanged(0, selectedPhotoList.size());
    }

}
