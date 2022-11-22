package com.iskyun.im.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.UploadRepository;
import com.iskyun.im.data.bean.AppVersion;
import com.iskyun.im.data.bean.DownloadInfo;
import com.iskyun.im.data.net.RetrofitManager;
import com.iskyun.im.utils.AppUtils;
import com.iskyun.im.utils.FileUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.download.DownloadProgressHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class VersionViewModel extends BaseViewModel<List<AppVersion>> {

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final UploadRepository uploadRepository;

    private final MutableLiveData<AppVersion> versionLiveData = new MutableLiveData<>();
    //
    private boolean showNewestTips = false;

    public VersionViewModel() {
        showDialog = false;
        uploadRepository = UploadRepository.get();
    }

    @Override
    public void onSuccess(List<AppVersion> appVersions) {
        if (!appVersions.isEmpty()) {
            compareVersion(appVersions.get(0));
        }
    }

    public LiveData<AppVersion> observerNewVersion() {
        uploadRepository.updateVersion().map(this).subscribe(this);
        return versionLiveData;
    }

    /**
     * 版本比较
     *
     * @param appVersion
     */
    private void compareVersion(AppVersion appVersion) {
        int localCode = AppUtils.getVersionCode();
        if (localCode < appVersion.getVersionCode()) {
            versionLiveData.postValue(appVersion);
        }else if(showNewestTips){
            ToastUtils.showToast(R.string.latest_version);
        }
    }

    public void showNewestTips(boolean showTips){
        this.showNewestTips = showTips;
    }

    /**
     * 下载
     *
     * @param url
     * @param progressHandler
     */
    public void download(String url, DownloadProgressHandler progressHandler) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        DownloadInfo downloadInfo = new DownloadInfo();
        RetrofitManager.get().getApiUploadService().download(url)
                .flatMap((Function<ResponseBody, ObservableSource<DownloadInfo>>) responseBody -> Observable.create((ObservableOnSubscribe<DownloadInfo>) emitter -> {
                    InputStream inputStream = null;
                    long total = 0;
                    long responseLength = 0;
                    FileOutputStream fos = null;
                    try {
                        byte[] buf = new byte[1024 * 16];
                        int len = 0;
                        responseLength = responseBody.contentLength();
                        inputStream = responseBody.byteStream();

                        String fileName = AppUtils.getPackageName() + "-" + AppUtils.getVersionName() + ".apk";
                        final File file = new File(FileUtils.getDownloadDir(), fileName);
                        downloadInfo.setFile(file);
                        downloadInfo.setFileSize(responseLength);
                        fos = new FileOutputStream(file);
                        int progress = 0;
                        int lastProgress = 0;
                        long startTime = System.currentTimeMillis();
                        while ((len = inputStream.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            total += len;
                            lastProgress = progress;
                            progress = (int) (total * 100 / responseLength);
                            long curTime = System.currentTimeMillis();
                            long usedTime = (curTime - startTime) / 1000;
                            if (usedTime == 0) {
                                usedTime = 1;
                            }
                            long speed = (total / usedTime); // 平均每秒下载速度
                            // 如果进度与之前进度相等，则不更新，如果更新太频繁，则会造成界面卡顿
                            if (progress > 0 && progress != lastProgress) {
                                downloadInfo.setSpeed(speed);
                                downloadInfo.setProgress(progress);
                                downloadInfo.setCurrentSize(total);
                                downloadInfo.setUsedTime(usedTime);
                                if (!emitter.isDisposed()) {
                                    emitter.onNext(downloadInfo);
                                }
                            }
                        }
                        fos.flush();
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    } catch (Exception e) {
                        downloadInfo.setErrorMsg(e);
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        progressHandler.onProgress(downloadInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressHandler.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        progressHandler.onCompleted(downloadInfo.getFile());
                    }
                });

    }

    public void cancel() {
        mDisposable.clear();
    }
}
