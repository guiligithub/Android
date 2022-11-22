package com.iskyun.im.ui.message.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.bean.OnlineService;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

public class OnlineViewModel extends BaseListViewModel<ListRecords<OnlineService>> {

    private MutableLiveData<List<OnlineService>> onlineServiceLiveData = new MutableLiveData<>();


    @Override
    protected void getData() {
        getOnlineServiceForService();
    }

    @Override
    public void onSuccess(ListRecords<OnlineService> onlineServices) {
        if(onlineServices != null)
            onlineServiceLiveData.postValue(onlineServices.getRecords());
        /*if (onlineServices != null && !onlineServices.isEmpty()) {
            SpManager.getInstance().setInitOnlineService(true);
            DbHelper.getInstance().getOnlineServiceDao().deleteAllOnlineService();
            DbHelper.getInstance().getOnlineServiceDao().insert(onlineServices);
        }*/
    }

    /**
     * 本地数据
     * @return
     */
    public List<OnlineService> getOnlineServiceForLocal() {
        return null;
    }

    /**
     *
     */
    public void getOnlineService(){
        List<OnlineService> onlineService = getOnlineServiceForLocal();
        if(onlineService == null || onlineService.isEmpty()){
            getOnlineServiceForService();
        }else {
            onlineServiceLiveData.postValue(onlineService);
        }
    }

    /**
     * 服务器数据
     */
    public void getOnlineServiceForService() {
        setShowDialog(false);
        userRepository.getOnlineService().map(this).subscribe(this);
    }

    public LiveData<List<OnlineService>> observerOnline(){
        return onlineServiceLiveData;
    }
}
