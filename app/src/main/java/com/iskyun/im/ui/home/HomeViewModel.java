package com.iskyun.im.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.Banner;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.resp.ListRecords;
import com.iskyun.im.helper.ImHelper;

import java.util.List;

public class HomeViewModel extends BaseListViewModel<ListRecords<Anchor>> {

    private final MutableLiveData<List<Anchor>> liveData;
    private final MutableLiveData<User> userOnlineLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> unReadLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Banner>> bannerLiveData = new MutableLiveData<>();
    private int tag;

    public HomeViewModel() {
        super();
        showDialog = false;
        liveData = new MutableLiveData<>();
    }

    @Override
    public void onSuccess(ListRecords<Anchor> anchors) {
        if (anchors.getRecords() != null)
            liveData.postValue(anchors.getRecords());
    }

    private void getAnchor() {
        userRepository.getAnchors(tag, page, PAGE_SIZE).map(this).subscribe(this);
    }

    public LiveData<List<Anchor>> getAnchors(int tag) {
        this.tag = tag;
        getAnchor();
        return liveData;
    }

    @Override
    public void getData() {
        getAnchor();
    }


    public void updateUserOnline(int tag){
        userRepository.updateUserOnline(tag).map(new HttpResultFunc<>())
                .subscribe(this::onUserOnlineNext,this::onError,this::onComplete);
    }

    public LiveData<User> observerUserOnline(){
        return userOnlineLiveData;
    }

    private void onUserOnlineNext(User user) {
        userOnlineLiveData.postValue(user);
    }

    public void checkUnreadMsg() {
        int unreadMessageCount = ImHelper.getInstance().getChatManager().getUnreadMessageCount();
        unReadLiveData.postValue(unreadMessageCount);
    }

    public LiveData<Integer> unReadObserve() {
        return unReadLiveData;
    }

    public LiveData<List<Banner>> getBanners(){
        setShowDialog(false);
        userRepository.getBanners().map(new HttpResultFunc<>())
                .subscribe(this::onBannerNext,this::onError, this::onComplete);
        return bannerLiveData;
    }

    private void onBannerNext(List<Banner> banners){
        bannerLiveData.postValue(banners);
    }

}