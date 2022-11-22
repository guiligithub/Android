package com.iskyun.im.ui.mine.frag;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.VisitorTabAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.databinding.FragmentBroeswVisitorBinding;
import com.iskyun.im.databinding.FragmentBroeswVisitorItemBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.mine.viewmodel.UserRelationsModel;
import com.iskyun.im.utils.event.RelationEvent;

import java.util.ArrayList;
import java.util.List;

public class VisitorFragment extends BaseListFragment<FragmentBroeswVisitorBinding, UserRelationsModel> {
    private VisitorTabAdapter visitorTabAdapter;
    private List<UserRelations> data;

    @Override
    public UserRelationsModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(UserRelationsModel.class);
    }

    @Override
    public FragmentBroeswVisitorBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBroeswVisitorBinding.inflate(inflater);
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    protected BaseQuickAdapter<UserRelations, BaseDataBindingHolder<FragmentBroeswVisitorItemBinding>> onCreateAdapter() {
        visitorTabAdapter=new VisitorTabAdapter(R.layout.fragment_broesw_visitor_item);
        return visitorTabAdapter;
    }

    @Override
    public void initBase() {
        super.initBase();
        data = new ArrayList<>();
        mViewModel.getUserRelations(RelationType.VISIT).observe(this, userRelations -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(userRelations);
            visitorTabAdapter.setList(data);
        });
    }

    @Override
    public void onDestroy() {
        RelationEvent event = new RelationEvent(0);
        LiveDataBus.get().with(Constant.RELATION_VISITOR,RelationEvent.class).postValue(event);
        super.onDestroy();
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}


