package com.iskyun.im.viewmodel;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.UploadRepository;
import com.iskyun.im.data.bean.IdCard;
import com.iskyun.im.data.bean.PhotoDiscern;
import com.iskyun.im.data.bean.SignatureDirect;
import com.iskyun.im.data.bean.StsToken;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FileUploadViewModel extends BaseViewModel<List<String>> {

    private final UploadRepository uploadRepository;
    private final MutableLiveData<List<String>> pathLiveData = new MutableLiveData<>();
    private final MutableLiveData<SignatureDirect> signatureLiveData = new MutableLiveData<>();
    private final MutableLiveData<StsToken.Credentials> stsTokenLiveData = new MutableLiveData<>();
    private final MutableLiveData<IdCard> idCardLiveData = new MutableLiveData<>();
    private final MutableLiveData<PhotoDiscern> photoScanLiveData = new MutableLiveData<>();
    private IdCard rIdCard = new IdCard();
    private String compressImageUriPath;

    public FileUploadViewModel() {
        super();
        uploadRepository = UploadRepository.get();
    }

    @Override
    public void onSuccess(List<String> strings) {
        pathLiveData.setValue(strings);
    }

    public void uploads(List<String> paths) {
        uploadRepository.uploadImages(paths).map(this).subscribe(this);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if(!TextUtils.isEmpty(compressImageUriPath)){
            FileUtils.deleteImageFile(compressImageUriPath);
            compressImageUriPath="";
        }
    }

    /**
     * 图片鉴别
     *
     * @param uriPath
     */
    public void photoDiscern(String uriPath) {
        if (TextUtils.isEmpty(uriPath)) {
            return;
        }
        setShowDialog(true);
        showDialog();
        Observable<String> observable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String compressUriPath = FileUtils.compressImage(uriPath);
            compressImageUriPath = compressUriPath;
            String filePath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                filePath = FileUtils.getRealPathFromURI(Uri.parse(compressUriPath));
            } else {
                filePath = uriPath;
            }
            emitter.onNext(filePath);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());

        observable.flatMap((Function<String, ObservableSource<AppResponse<List<PhotoDiscern>>>>) s -> uploadRepository.photoScanByFile(s))
                .map(new HttpResultFunc<>()).subscribe(this::photoScanNext, this::onError, this::onComplete);
    }

    public LiveData<PhotoDiscern> observerPhotoScan() {
        return photoScanLiveData;
    }

    private void photoScanNext(List<PhotoDiscern> photoDiscerns) {
        //删除压缩后的图片
        if(!TextUtils.isEmpty(compressImageUriPath)){
            FileUtils.deleteImageFile(compressImageUriPath);
            compressImageUriPath = "";
        }
        if (!photoDiscerns.isEmpty()) {
            photoScanLiveData.postValue(photoDiscerns.get(0));
        }
    }

    /**
     * 身份证分两次传
     *
     * @param idCard
     */
    public void uploadIdCart(IdCard idCard) {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        List<String> faceList = new ArrayList<>();
        faceList.add(idCard.getIdCardFaceUrl());
        uploadRepository.uploadImages(faceList)
                .flatMap((Function<AppResponse<List<String>>, ObservableSource<AppResponse<List<String>>>>) response -> {
                    if (response.getData() != null)
                        rIdCard.setIdCardFaceUrl(response.getData().get(0));
                    List<String> backList = new ArrayList<>();
                    backList.add(idCard.getIdCardBackUrl());
                    return uploadRepository.uploadImages(backList);
                })
                .map(this).subscribe(this::uploadIdCart);
    }

    public LiveData<IdCard> observerIdCard() {
        return idCardLiveData;
    }

    private void uploadIdCart(List<String> strings) {
        rIdCard.setIdCardBackUrl(strings.get(0));
        idCardLiveData.postValue(rIdCard);
    }

    public LiveData<List<String>> observerPaths() {
        return pathLiveData;
    }

    public LiveData<SignatureDirect> getSignatureDirect() {
        uploadRepository.signatureDirect().map(response -> {
            if (response != null) {
                return response.getData();
            }
            return null;
        }).subscribe(this::signatureNext, this::onError, this::onComplete);

        return signatureLiveData;
    }

    public LiveData<StsToken.Credentials> getStsToken() {
        uploadRepository.getStsToken().map(response -> {
            if (response != null) {
                return response.getData();
            }
            return null;
        }).subscribe(this::stsToken, this::onError, this::onComplete);
        return stsTokenLiveData;
    }


    private void stsToken(StsToken stsToken) {
        if (stsToken != null)
            stsTokenLiveData.setValue(stsToken.getCredentials());
    }

    private void signatureNext(SignatureDirect signatureDirect) {
        if (signatureDirect != null) {
            signatureLiveData.setValue(signatureDirect);
        }
    }

}
