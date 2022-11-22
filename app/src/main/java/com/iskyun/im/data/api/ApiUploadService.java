package com.iskyun.im.data.api;

import com.iskyun.im.data.bean.AppVersion;
import com.iskyun.im.data.bean.PhotoDiscern;
import com.iskyun.im.data.bean.SignatureDirect;
import com.iskyun.im.data.bean.StsToken;
import com.iskyun.im.data.resp.AppResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiUploadService {

    @Multipart
    @POST("common/fileUpload/upload")
    Observable<AppResponse<String>> uploadImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("common/fileUpload/upload")
    Observable<AppResponse<List<String>>> uploadImages(
            @Part List<MultipartBody.Part> parts);

    /**
     * 签名直传  video
     * @param tag 1 视频
     * @return
     */
    @GET("common/fileUpload/signatureDirect")
    Observable<AppResponse<SignatureDirect>> signatureDirect(
            @Query("tag") int tag);


    /**
     * 文件上传sts服务
     */
    @GET("common/fileUpload/STSAssumeRole")
    Observable<AppResponse<StsToken>> getStsToken();

    /**
     * 版本
     */
    @GET("common/appVersions/getNewVersion")
    Observable<AppResponse<List<AppVersion>>> updateVersion(@Query("osType") String osType);

    /**
     * 图片鉴别
     * @param file
     * @return
     */
    @Multipart
    @POST("common/ali-jianghuang/photoScanByFile")
    Observable<AppResponse<List<PhotoDiscern>>> photoScanByFile(@Part MultipartBody.Part file);

    /**
     * 下载文件
     *
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
