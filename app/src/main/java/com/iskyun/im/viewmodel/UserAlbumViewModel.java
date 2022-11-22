package com.iskyun.im.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.UploadRepository;
import com.iskyun.im.data.bean.UserAlbum;
import com.iskyun.im.data.req.AlbumBody;
import com.iskyun.im.data.resp.AppResponse;

import java.util.List;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class UserAlbumViewModel extends BaseViewModel<List<UserAlbum>> {

    private final MutableLiveData<List<UserAlbum>> userAlbumLiveData = new MutableLiveData<>();
    private UploadRepository uploadRepository;

    public UserAlbumViewModel(){
        uploadRepository = UploadRepository.get();
    }

    @Override
    public void onSuccess(List<UserAlbum> userAlbums) {
        userAlbumLiveData.postValue(userAlbums);
    }

    public void userPhotos(AlbumBody body, boolean showDialog){
        super.showDialog = showDialog;
        userRepository.userPhotos(body).map(this).subscribe(this);
    }

    public void userUploadPhotos(AlbumBody body){
        uploadRepository.uploadImages(body.getPhotoList())
                .flatMap((Function<AppResponse<List<String>>, ObservableSource<AppResponse<List<UserAlbum>>>>) listAppResponse -> {
                    if(listAppResponse.getData() != null){
                        body.setPhotoList(listAppResponse.getData());
                        return userRepository.userPhotos(body);
                    }
                    return null;
                }).map(this).subscribe(this);
    }

    public LiveData<List<UserAlbum>> observerUserPhotos(){
        return userAlbumLiveData;
    }

}
