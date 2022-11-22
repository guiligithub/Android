package com.iskyun.im.ui.message.frag;

import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.CallLogAdapter;
import com.iskyun.im.adapter.CircularImageGridAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.CallLog;
import com.iskyun.im.databinding.FragmentCallLogBinding;
import com.iskyun.im.databinding.ViewItemCallLogBinding;
import com.iskyun.im.ui.message.viewmodel.CallLogViewModel;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CallLogFragment extends BaseListFragment<FragmentCallLogBinding, CallLogViewModel> {
    private CallLogAdapter callLogAdapter;
    private final List<CallLog> data = new ArrayList<>();

    @Override
    protected BaseQuickAdapter<CallLog, BaseDataBindingHolder<ViewItemCallLogBinding>> onCreateAdapter() {
        callLogAdapter = new CallLogAdapter(R.layout.view_item_call_log);
        return callLogAdapter;
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    public CallLogViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(CallLogViewModel.class);
    }

    @Override
    public FragmentCallLogBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentCallLogBinding.inflate(inflater);
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    public void initBase() {
        super.initBase();

        RecyclerView.ItemDecoration decoration = new GridItemDecoration(getActivity(),
                getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.white);
        mViewBinding.oftenCallRecyclerView.addItemDecoration(decoration);
        mViewBinding.oftenCallRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        CircularImageGridAdapter imageGridAdapter = new CircularImageGridAdapter();
        mViewBinding.oftenCallRecyclerView.setAdapter(imageGridAdapter);

        refreshLayout.setRefreshing(true);
        mViewModel.setShowDialog(false);
        mViewModel.getCallLog().observe(this, callLogs -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(callLogs);
            callLogAdapter.setList(data);

            ArrayList<String> list=new ArrayList<>();
            for(int i = 0;i < data.size(); i++){
                list.add(data.get(i).getHeadUrl());
            }

            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = list.size() - 1; j > i; j--) {
                    if (list.get(j).toString().equals(list.get(i).toString())) {
                        list.remove(j);//删除重复元素
                    }
                }
            }

            if (list.size()>=5){
                List<String> mPhotoUrlList = list.subList(0, 5);
                imageGridAdapter.setList(mPhotoUrlList);
            }else {
               List<String> mPhotoUrlList = list.subList(0, list.size());
                imageGridAdapter.setList(mPhotoUrlList);
            }

        });
    }
}
