package com.iskyun.im.ui.dynamic.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.sdk.android.oss.OSS;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.UploadRepository;
import com.iskyun.im.data.bean.Dynamic;
import com.iskyun.im.data.bean.StsToken;
import com.iskyun.im.data.net.AppException;
import com.iskyun.im.data.req.DynamicBody;
import com.iskyun.im.data.req.FileUploadType;
import com.iskyun.im.data.req.InteractionBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.data.resp.ListRecords;
import com.iskyun.im.ui.dialog.AppLoadingDialog;
import com.iskyun.im.ui.dynamic.frag.DynamicTabFragment;
import com.iskyun.im.utils.FileUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.OSSUploadUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.SpManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class DynamicViewModel extends BaseListViewModel<ListRecords<Dynamic>> {

    private final MutableLiveData<List<Dynamic>> liveData;
    private final MutableLiveData<String> publishLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> interactionLiveData = new MutableLiveData<>();
    private final UploadRepository uploadRepository;
    private OSS oss;
    private int dynamicType;
    private int userId;
    private AppLoadingDialog publishLoadingDialog;

    public DynamicViewModel() {
        super();
        showDialog = false;
        publishLoadingDialog = AppLoadingDialog.get(ImLiveApp.get().getTopActivity());
        liveData = new MutableLiveData<>();
        uploadRepository = UploadRepository.get();
    }

    @Override
    public void onSuccess(ListRecords<Dynamic> dynamics) {
        if (dynamics.getRecords() != null)
            liveData.postValue(dynamics.getRecords());
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if (publishLoadingDialog != null && publishLoadingDialog.isShowing()) {
            publishLoadingDialog.cancel();
        }
    }


    @Override
    public void onComplete() {
        super.onComplete();
        if (publishLoadingDialog != null && publishLoadingDialog.isShowing()) {
            publishLoadingDialog.cancel();
        }
    }

    @Override
    protected void getData() {
        getDynamics(this.dynamicType,this.userId);
    }

    //获取动态
    public LiveData<List<Dynamic>> getDynamics(int dynamicType,int userid) {
        userId=userid;
        if (dynamicType == DynamicTabFragment.MINE_DYNAMIC) {
            userId = SpManager.getInstance().getCurrentUser().getId();
        }else if(dynamicType == DynamicTabFragment.PERSONAL_DYNAMIC){
            userId = userid;
        }
        this.dynamicType = dynamicType;
        userRepository.getDynamics(userId, page, PAGE_SIZE).map(this).subscribe(this);
        return liveData;
    }

    //点赞
    public void userInteraction(InteractionBody body) {
        userRepository.userInteraction(body)
                .map(new HttpResultFuncStr())
                .subscribe(this::interaction, this::onError, this::onComplete);
    }

    public LiveData<String> observerPublish() {
        return publishLiveData;
    }

    public LiveData<String> observerInteraction() {
        return interactionLiveData;
    }

    /**
     * 发布动态
     *
     * @param body
     */
    public void publishDynamic(@NonNull List<Photo> selectedList, DynamicBody body) {
        if (TextUtils.isEmpty(body.getTextDetails())
                || TextUtils.isEmpty(body.getTextDetails().trim())) {
            ToastUtils.showToast(R.string.input_content);
            return;
        }
        if (selectedList.isEmpty()) {
            ToastUtils.showToast(R.string.share_picture_or_video);
            return;
        }
        int uploadType;
        if (selectedList.get(0).type.startsWith("video/")) {
            uploadType = FileUploadType.VIDEO.getUploadType();
        } else {
            uploadType = FileUploadType.IMAGE.getUploadType();
        }
        body.setFileType(uploadType);

        if(publishLoadingDialog != null && !publishLoadingDialog.isShowing()){
            publishLoadingDialog.show();
        }

        if (uploadType == FileUploadType.VIDEO.getUploadType()) {
            Observable<String> compressObservable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                String compressVideoPath = FileUtils.compressVideo(selectedList.get(0).path);
                emitter.onNext(compressVideoPath);
                emitter.onComplete();
            }).subscribeOn(Schedulers.io());

            uploadRepository.getStsToken().flatMap((Function<AppResponse<StsToken>, ObservableSource<String>>) response -> {
                        LogUtils.e("------stsTokenObservable-------");
                        if (response == null || response.getData() == null)
                            return null;
                        StsToken.Credentials credentials = response.getData().getCredentials();
                        oss = OSSUploadUtils.createOssClient(credentials.getAccessKeyId(),
                                credentials.getAccessKeySecret(),
                                credentials.getSecurityToken());
                        return compressObservable;
                    }).flatMap((Function<String, ObservableSource<String>>) s -> {
                        LogUtils.e("------ossUpload-------");
                        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
                            String ossPath = OSSUploadUtils.ossUpload(oss, s);
                            emitter.onNext(ossPath);
                            emitter.onComplete();
                        });
                    }).observeOn(Schedulers.io()).flatMap((Function<String, ObservableSource<AppResponse<String>>>) s -> {
                        LogUtils.e("------publishDynamic-------");
                        List<Dynamic.FileType> list = new ArrayList<>();
                        Dynamic.FileType fileType = new Dynamic.FileType();
                        //fileType.setFileType(uploadType);
                        fileType.setFileUrl(s);
                        list.add(fileType);
                        body.setAppCommonFileList(list);
                        return userRepository.publishDynamic(body);
                    }).map(new HttpResultFuncStr())
                    .subscribe(this::onPublishDynamicVideo, this::onError, this::onComplete);

        } else {
            List<String> urls = new ArrayList<>();
            for (Photo photo : selectedList) {
                urls.add(photo.path);
            }
            uploadRepository.uploadImages(urls)
                    .flatMap((Function<AppResponse<List<String>>, ObservableSource<AppResponse<String>>>) resp -> {
                        if (resp.getData() != null) {
                            List<Dynamic.FileType> list = new ArrayList<>();
                            for (String url : resp.getData()) {
                                Dynamic.FileType fileType = new Dynamic.FileType();
                                fileType.setFileUrl(url);
                                //fileType.setFileType(uploadType);
                                list.add(fileType);
                            }
                            body.setAppCommonFileList(list);
                            return userRepository.publishDynamic(body);
                        }
                        throw new AppException(resp.getMsg(), resp.getCode());
                    }).map(response -> {
                        if (response.getCode() == 200) {
                            return "success";
                        }
                        throw new AppException(response.getMsg(), response.getCode());
                    }).subscribe(this::onPublishDynamic, this::onError, this::onComplete);
        }

    }

    private void interaction(String response) {
        interactionLiveData.postValue(response );
    }

    private void onPublishDynamicVideo(String s) {
        publishLiveData.postValue(s);
    }


    private void onPublishDynamic(String s) {
        publishLiveData.postValue(s);
    }


}

