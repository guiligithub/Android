package com.iskyun.im.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.MainAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.AuthStatus;
import com.iskyun.im.data.bean.UserAlbum;
import com.iskyun.im.data.req.AlbumBody;
import com.iskyun.im.data.req.AlbumTag;
import com.iskyun.im.databinding.ActivityMineAlbumBinding;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.ui.common.PreviewPicturesActivity;
import com.iskyun.im.viewmodel.UserAlbumViewModel;

import java.util.ArrayList;
import java.util.List;
/**
 * 我的相册
 * */
public class MineAlbumActivity extends BaseActivity<ActivityMineAlbumBinding, UserAlbumViewModel> {
    private static final int NUMBER_OF_SIX = 6;
    public static final String nameFlag = "nameFlag";

    /**
     * 选择的图片集
     */
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private final List<Photo> allPhotos = new ArrayList<>();
    private final List<Photo> remotePhotos = new ArrayList<>();
    private MainAdapter adapter;
    private AlbumBody albumBody;
    private int deletePosition;


    @Override
    public UserAlbumViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(UserAlbumViewModel.class);
    }

    @Override
    public ActivityMineAlbumBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityMineAlbumBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        initView();

        albumBody = new AlbumBody();
        albumBody.setTag(AlbumTag.MY_ALBUM.getTag());
        mViewModel.userPhotos(albumBody, true);

        mViewModel.observerUserPhotos().observe(this, userAlbums -> {
            if(albumBody.getTag() == AlbumTag.MY_ALBUM.getTag()){
                for (UserAlbum album:userAlbums) {
                    Photo photo = new Photo();
                    photo.name = album.getId()+nameFlag;
                    photo.path = album.getFileUrl();
                    photo.type = "image";
                    photo.status = album.getAuthStatus();
                    remotePhotos.add(photo);
                }
                allPhotos.addAll(remotePhotos);
                adapter.notifyItemRangeChanged(0, allPhotos.size());
            }else if(albumBody.getTag() == AlbumTag.ADD_ALBUM.getTag()){
                for (Photo photo:selectedPhotoList) {
                    photo.status = AuthStatus.NO_APPROVAL.getAuthStatus();
                }
                selectedPhotoList.clear();
                adapter.notifyItemRangeChanged(remotePhotos.size(), allPhotos.size());
            }else if(albumBody.getTag() == AlbumTag.DELETE_ALBUM.getTag()){
                adapter.delete(deletePosition);
            }
        });
    }

    private void initView() {
        /*RecyclerView.ItemDecoration decoration = new GridItemDecoration(this,
                getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.white);
        mViewBinding.mineAlbumRecyclerview.addItemDecoration(decoration);*/
        mViewBinding.mineAlbumRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);

        adapter = new MainAdapter();
        adapter.setList(allPhotos);
        adapter.setSelectMax(NUMBER_OF_SIX);
        mViewBinding.mineAlbumRecyclerview.setAdapter(adapter);

        adapter.setOnItemDelListener(new MainAdapter.OnItemDelListener() {
            @Override
            public void onDeleteNetPhoto(Photo photo, int position) {
                deletePosition = position;
                if(photo.name != null && photo.name.endsWith(nameFlag)){
                    List<String> list = new ArrayList<>();
                    list.add(photo.name.replace(nameFlag,""));
                    albumBody.setCommonFileId(list);
                    albumBody.setTag(AlbumTag.DELETE_ALBUM.getTag());
                    mViewModel.userPhotos(albumBody, true);
                }
            }

            @Override
            public void onDeleteLocalPhoto(int position) {
                Photo photo = allPhotos.get(position);
                selectedPhotoList.remove(photo);
            }
        });

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ArrayList<String> list=new ArrayList<>();
                for(int i = 0;i < allPhotos.size(); i++){
                    list.add(allPhotos.get(i).path);
                }
                PreviewPicturesActivity.start(MineAlbumActivity.this, 0, position, list);
            }

            @Override
            public void onPictureSelect() {
                if (PermissionUtil.checkAndRequestPermissionsInActivity(ImLiveApp.get().getTopActivity(),
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_USERINFO_ALBUM);
                    fragment.setOnItemClickListener(MineAlbumActivity.this::onItemClick);
                    fragment.show(getSupportFragmentManager(), "album");
                }
            }
        });

        mViewBinding.userInfoSave.setOnClickListener(this::onClick);
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.user_info_save) {
            if (selectedPhotoList.isEmpty()) {
                return;
            }
            saveAlbums();
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent());
        super.finish();
    }

    private void saveAlbums(){
        albumBody.setTag(AlbumTag.ADD_ALBUM.getTag());
        List<String > paths = new ArrayList<>();
        for (Photo p : selectedPhotoList) {
            paths.add(p.path);
        }
        albumBody.setPhotoList(paths);
        mViewModel.userUploadPhotos(albumBody);
    }

    private void onItemClick(MoreActionFragment.MoreType type) {
        switch (type) {
            case ALBUM:
                EasyPhotos.createAlbum(MineAlbumActivity.this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .setCount(NUMBER_OF_SIX)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (requestCode == UploadType.ALBUM.getValue()) {
            onActivityResult(resultPhotos);
        } else if (requestCode == UploadType.CAMERA_PIC.getValue()) {
            onActivityResult(resultPhotos);
        }
    }

    private void onActivityResult(ArrayList<Photo> photos) {
        allPhotos.removeAll(selectedPhotoList);
        selectedPhotoList.clear();
        selectedPhotoList.addAll(photos);
        allPhotos.addAll(selectedPhotoList);
        adapter.notifyItemRangeChanged(remotePhotos.size(), allPhotos.size());
    }


    @Override
    public int getTitleText() {
        return R.string.mine_album;
    }
}
