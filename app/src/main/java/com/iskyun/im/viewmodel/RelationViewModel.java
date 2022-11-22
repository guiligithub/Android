package com.iskyun.im.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.bean.Blacklist;
import com.iskyun.im.data.req.RelationBody;

import java.util.List;


public class RelationViewModel extends BaseListViewModel<List<Blacklist>> {

   
    private final MutableLiveData<String> addLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Blacklist>> blacklistLiveData = new MutableLiveData<>();
 
    public void onSuccess(List<Blacklist> relation) {
        blacklistLiveData.postValue(relation);
    }

    /**
     * 添加用户关系
     * @param body
     */
    public void addUserRelation(RelationBody body){
        UserRepository.get().addUserRelation(body).map(new HttpResultFuncStr())
                .subscribe(this::onUserRelation, this::onError, this::onComplete);
    }

    private void onUserRelation(String s) {
        addLiveData.postValue(s);
    }

    /**
     * 删除用户关系
     * @param body
     */
    public void delAttention(RelationBody body){
        UserRepository.get().delAttention(body).map(new HttpResultFuncStr())
                .subscribe(this::onUserRelation, this::onError, this::onComplete);
    }

    /**
     * 用户关系订阅
     * @return
     */
    public LiveData<String> observerRelation(){
        return addLiveData;
    }

    /**
     *
     */
    @Override
    protected void getData() {
        getBlacklist();
    }

    /**
     * 黑名单
     */
    public void getBlacklist(){
        setShowDialog(false);
        userRepository.blacklist().map(this).subscribe(this);
    }

    /**
     * 黑名单订阅
     * @return
     */
    public LiveData<List<Blacklist>> observerBlacklist(){
        return blacklistLiveData;
    }

}
