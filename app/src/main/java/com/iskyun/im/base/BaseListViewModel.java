package com.iskyun.im.base;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.iskyun.im.data.resp.ListRecords;
import com.iskyun.im.utils.LogUtils;

public abstract class BaseListViewModel<T> extends BaseViewModel<T> {

    public static final int NORMAL_PAGE = 1;
    public static final int PAGE_SIZE = 30;
    protected int page = NORMAL_PAGE;
    //
    private final MediatorLiveData<Status> mStatus = new MediatorLiveData<>();
    private final ObservableBoolean isRefresh = new ObservableBoolean();

    protected BaseListViewModel() {
        super();
    }

    public void loadData(Status status){
        LogUtils.e("loadData--status:"+status.status);
        if (status == Status.ON_REFRESH) {
            if (mStatus.getValue() == Status.ON_REFRESH) {
                mStatus.setValue(Status.ON_REFRESHING);
                return;
            }
            page = NORMAL_PAGE;
            mStatus.setValue(status);
            isRefresh.set(true);
            getData();
        } else if (status == Status.ON_REFRESHING) {
            LogUtils.e("on_refreshing");
        } else if (status == Status.ON_LOAD_MORE) {
            if (mStatus.getValue() == Status.ON_LOAD_MORE
                    || mStatus.getValue() == Status.ON_NO_MORE
                    || mStatus.getValue() == Status.ON_REFRESH
                    || mStatus.getValue() == Status.ON_REFRESHING) {
                Status curStatus = mStatus.getValue();
                mStatus.setValue(curStatus);
                return;
            }
            page++;
            isRefresh.set(false);
            mStatus.setValue(status);
            getData();
        } else if (status == Status.ON_COMPLETED) {
            LogUtils.e("on_completed");
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        //mStatus.setValue(Status.ON_COMPLETED);
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
        /**
         * 加载完成判断
         */
        if(t instanceof ListRecords){
            ListRecords<?> listRecords = (ListRecords<?>) t;
            if (listRecords.getCurrent() == listRecords.getPages()) {
                mStatus.setValue(Status.ON_NO_MORE);
            } else {
                mStatus.setValue(Status.ON_COMPLETED);
            }
        }else {
            mStatus.setValue(Status.ON_NO_MORE);
        }
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        mStatus.setValue(Status.ON_FAIL);
    }

    public LiveData<Status> observerStatus(){
        return mStatus;
    }

    public boolean isRefresh(){
        return isRefresh.get();
    }

    public int getPage(){
        return this.page;
    }

    protected abstract void getData();


    /**
     * 列表加载请求状态
     */
    public enum Status {
        ON_REFRESH(0),
        ON_REFRESHING(1),
        ON_LOAD_MORE(2),
        ON_COMPLETED(3),
        ON_NO_MORE(4),
        ON_FAIL(5);

        private int status;

        Status(int status) {
            this.status = status;
        }
    }
}
