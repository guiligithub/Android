package com.iskyun.im.ui.common.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.UploadRepository;
import com.iskyun.im.data.bean.Complaint;
import com.iskyun.im.data.net.AppException;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class ComplaintViewModel extends BaseViewModel<Complaint>{
    private final MutableLiveData<String> publishLiveData = new MutableLiveData<>();
    private final UploadRepository uploadRepository=UploadRepository.get();
    private final MutableLiveData<Complaint> updateObserver= new MutableLiveData<>() ;


    @Override
    public void onSuccess(Complaint complaint) {
       updateObserver.setValue(complaint);
    }

    public LiveData<Complaint> observerUpdate(){
        return updateObserver;
    }

    /**
     * 提交
     *
     * @param body
     */
    public void publishDynamic(@NonNull List<Photo> selectedList, Complaint body) {
        if (body.getInformerId()==null&&body.getInformerId().isEmpty()) {
            ToastUtils.showToast(R.string.enter_user_id);
            return;
        }
        if (body.getTelephoneNumber()==null&&body.getTelephoneNumber().isEmpty()) {
            ToastUtils.showToast(R.string.enter_contact_number);
            return;
        }
        if (body.getReportReasons()==0) {
            ToastUtils.showToast(R.string.reason_for_reporting);
            return;
        }
        if (body.getReportExplain()==null&&body.getReportExplain().isEmpty()) {
            ToastUtils.showToast(R.string.enter_reason_for_reporting);
            return;
        }

            List<String> urls = new ArrayList<>();
            for (Photo photo : selectedList) {
                urls.add(photo.path);
            }
            uploadRepository.uploadImages(urls)
                    .flatMap((Function<AppResponse<List<String>>, ObservableSource<AppResponse<String>>>) resp -> {
                        if (resp.getData() != null) {
                            List<Complaint.FileType> list = new ArrayList<>();
                            for (String url : resp.getData()) {
                                Complaint.FileType fileType = new Complaint.FileType();
                                fileType.setFileUrl(url);
                                //fileType.setFileType(uploadType);
                                list.add(fileType);
                            }
                            body.setAppCommonFileList(list);
                            return userRepository.addComplaint(body);
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



