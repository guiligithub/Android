package com.iskyun.im.utils.download;

import com.iskyun.im.data.bean.DownloadInfo;

import java.io.File;

public interface DownloadCallBack {

    void onProgress(DownloadInfo downloadInfo);

    /**
     * 运行在主线程
     *
     * @param file
     */
    void onCompleted(File file);

    /**
     * 运行在主线程
     *
     * @param e
     */
    void onError(Throwable e);
}
