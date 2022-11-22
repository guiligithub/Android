package com.iskyun.im.viewmodel;

import com.iskyun.im.base.BaseListViewModel;

public class NearbyViewModel extends BaseListViewModel {
    @Override
    protected void getData() {

    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public Object apply(Object o) throws Throwable {
        return null;
    }

//    private final MutableLiveData<ArticleBean> liveData;
//
//    public NearbyViewModel() {
//        super();
//        liveData = new MutableLiveData<>();
//    }
//
//    @Override
//    public void onSuccess(ArticleBean articleBean) {
//        liveData.postValue(articleBean);
//    }
//
//    private void getTests(){
//       // userRepository.getTest(page).map(this).subscribe(this);
//    }
//
//    public LiveData<ArticleBean> getTest(){
//        getTests();
//        return liveData;
//    }
//
//    @Override
//    public void getData() {
//        LogUtils.e("getData");
//        getTests();
//    }
}