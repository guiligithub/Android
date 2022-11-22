package com.iskyun.im.data;

import androidx.annotation.NonNull;

import com.iskyun.im.data.api.ApiUploadService;
import com.iskyun.im.data.bean.AppVersion;
import com.iskyun.im.data.bean.PhotoDiscern;
import com.iskyun.im.data.bean.SignatureDirect;
import com.iskyun.im.data.bean.StsToken;
import com.iskyun.im.data.net.RetrofitManager;
import com.iskyun.im.data.resp.AppResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadRepository {

    private ApiUploadService uploadService;
    private volatile static UploadRepository INSTANCE;
    private final MediaType mediaType;

    private UploadRepository() {
        uploadService = RetrofitManager.get().getApiUploadService();
        mediaType = MediaType.parse("multipart/form-data");
    }

    public static UploadRepository get() {
        if (INSTANCE == null) {
            synchronized (UploadRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UploadRepository();
                }
            }
        }
        return INSTANCE;
    }


    public Observable<AppResponse<String>> upload(@NonNull String path) {
        /**
         * MultipartBody multipartBody = new MultipartBody.Builder()
         *                 .addFormDataPart("file", "fileName.jpg", body)
         *                 .setType(MultipartBody.FORM)
         *                 .build();
         */
        File file = new File(path);
        RequestBody body = RequestBody.create(mediaType, file);
        MultipartBody.Part mBody = MultipartBody.Part.createFormData("files", file.getName(), body);
        return uploadService.uploadImage(mBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 图片鉴别
     * @param path
     * @return
     */
    public Observable<AppResponse<List<PhotoDiscern>>> photoScanByFile(@NonNull String path) {
        File file = new File(path);
        RequestBody body = RequestBody.create(mediaType, file);
        MultipartBody.Part mBody = MultipartBody.Part.createFormData("files", file.getName(), body);
        return uploadService.photoScanByFile(mBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<List<String>>> uploadImages(@NonNull List<String> paths) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            RequestBody body = RequestBody.create(mediaType, file);
            MultipartBody.Part mBody = MultipartBody.Part.createFormData("files", file.getName(), body);
            parts.add(mBody);
        }
        return uploadService.uploadImages(parts).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<SignatureDirect>> signatureDirect() {
        return uploadService.signatureDirect(1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<StsToken>> getStsToken() {
        return uploadService.getStsToken().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<List<AppVersion>>> updateVersion() {
        return uploadService.updateVersion("android").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
