package com.iskyun.im.ui.common.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.UploadRepository;
import com.iskyun.im.data.bean.Suggest;
import com.iskyun.im.data.net.AppException;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class FeedbackViewModel extends BaseViewModel<Suggest> {
    private final MutableLiveData<String> publishLiveData = new MutableLiveData<>();
    private final UploadRepository uploadRepository=UploadRepository.get();
    private final MutableLiveData<Suggest> updateObserver= new MutableLiveData<>() ;


    @Override
    public void onSuccess(Suggest suggest) {
        updateObserver.setValue(suggest);
    }

    public LiveData<Suggest> observerUpdate(){
        return updateObserver;
    }

    /**
     * 提交
     *
     * @param body
     */
    public void publishSuggest(@NonNull List<Photo> selectedList, Suggest body) {
        if (body.getIdeaText()==null&&!body.getIdeaText().isEmpty()) {
            ToastUtils.showToast(R.string.enter_suggestions);
            return;
        }

        List<String> urls = new ArrayList<>();
        for (Photo photo : selectedList) {
            urls.add(photo.path);
        }
        uploadRepository.uploadImages(urls)
                .flatMap((Function<AppResponse<List<String>>, ObservableSource<AppResponse<String>>>) resp -> {
                    if (resp.getData() != null) {
                        List<Suggest.FileType> list = new ArrayList<>();
                        for (String url : resp.getData()) {
                            Suggest.FileType fileType = new Suggest.FileType();
                            fileType.setFileUrl(url);
                            //fileType.setFileType(uploadType);
                            list.add(fileType);
                        }
                        body.setImgList(list);
                        return userRepository.addSuggest(body);
                    }
                    throw new AppException(resp.getMsg(), resp.getCode());
                }).map(response -> {
                    if (response.getCode() == 200) {
                        ToastUtils.showToast(R.string.complaint_success);
                        return "success";
                    }
                    throw new AppException(response.getMsg(), response.getCode());
                }).subscribe(this::onPublishDynamic, this::onError, this::onComplete);
    }

    private void onPublishDynamic(String s) {
        publishLiveData.postValue(s);
    }
}



