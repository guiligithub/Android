package com.iskyun.im.utils;

import android.text.format.DateFormat;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.PartETag;
import com.alibaba.sdk.android.oss.model.UploadPartRequest;
import com.alibaba.sdk.android.oss.model.UploadPartResult;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.utils.manager.ExecutorManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class OSSUploadUtils {

    public static final String END_POINT = "https://oss-cn-hangzhou.aliyuncs.com";
    public static final String BUCKET_NAME = "huacao-api";
    public static final String PATH_KEY = "huacao_video/";

    public static OSS createOssClient(String accessKeyId,String accessKeySecret,
                                     String securityToken){
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                accessKeyId, accessKeySecret, securityToken);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setHttpDnsEnable(true);
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。
        return new OSSClient(ImLiveApp.get().getApplicationContext(),
                OSSUploadUtils.END_POINT,
                credentialProvider, conf);
    }


    public static String ossUpload(OSS oss, String uploadFilePath){
        String uploadLocation = null;
        String bucketName = BUCKET_NAME;
        File uploadFile = new File(uploadFilePath);
        String objectKey =  PATH_KEY + MD5.encrypt2MD5(DateFormat.format("yyyy_MM_dd_hh_mm_ss",
                Calendar.getInstance(Locale.CHINA)).toString())+uploadFile.getName();
        // 初始化分片上传。
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName,
                objectKey);
        InitiateMultipartUploadResult initResult = null;
        try {
            initResult = oss.initMultipartUpload(initRequest);
            // 返回uploadId，它是分片上传事件的唯一标识。您可以根据该uploadId发起相关操作，例如取消分片上传、查询分片上传等。
            String uploadId = initResult.getUploadId();
            // 设置单个Part的大小，单位为字节，取值范围为100 KB~5 GB。
            int partCount = 2 * 1000 * 1024;
            List<PartETag> partETags = new ArrayList<PartETag>(); // 保存分片上传的结果
            int partIndex = 1; // 上传分片编号，从1开始
            InputStream input = new FileInputStream(uploadFile);
            long fileLength = uploadFile.length();
            long uploadedLength = 0;
            while (uploadedLength < fileLength) {
                int partLength = (int) Math.min(partCount, fileLength - uploadedLength);
                byte[] partData = IOUtils.readStreamAsBytesArray(input, partLength); // 按照分片大小读取文件的一段内容
                UploadPartRequest uploadPart = new UploadPartRequest(bucketName, objectKey, uploadId, partIndex);
                uploadPart.setPartContent(partData); // 设置分片内容
                UploadPartResult uploadPartResult = oss.uploadPart(uploadPart);
                partETags.add(new PartETag(partIndex, uploadPartResult.getETag())); // 保存分片上传成功后的结果
                uploadedLength += partLength;
                partIndex++;
            }
            Collections.sort(partETags, (lhs, rhs) -> {
                if (lhs.getPartNumber() < rhs.getPartNumber()) {
                    return -1;
                } else if (lhs.getPartNumber() > rhs.getPartNumber()) {
                    return 1;
                } else {
                    return 0;
                }
            });

            CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partETags);
            CompleteMultipartUploadResult completeResult = oss.completeMultipartUpload(complete);
            LogUtils.e("completeResult:"+ GsonUtils.toJson(completeResult));
            uploadLocation = completeResult.getLocation();
        } catch (ClientException | ServiceException | IOException e) {
            LogUtils.e("上传失败:" + e.getMessage());
            ExecutorManager.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(R.string.upload_fail);
                }
            });
        }
        return uploadLocation;
    }
}
