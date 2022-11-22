package com.iskyun.im.ui.mine.frag;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.FollowTabAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.databinding.FragmentBroeswFollowBinding;
import com.iskyun.im.databinding.FragmentBroeswFollowTabBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.home.AnchorInfoActivity;
import com.iskyun.im.ui.mine.viewmodel.UserRelationsModel;
import com.iskyun.im.utils.event.RelationEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.RelationViewModel;

import java.util.ArrayList;
import java.util.List;

public class FollowFragment extends BaseListFragment<FragmentBroeswFollowBinding, UserRelationsModel> {
    private FollowTabAdapter followTabAdapter;
    private List<UserRelations> data;
    private RelationViewModel relationViewModel;
    private int attentionPosition;
    private UserRelations attentionAnchor;

    @Override
    public UserRelationsModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(UserRelationsModel.class);
    }

    @Override
    public FragmentBroeswFollowBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBroeswFollowBinding.inflate(inflater);
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    protected BaseQuickAdapter<UserRelations, BaseDataBindingHolder<FragmentBroeswFollowTabBinding>> onCreateAdapter() {
        followTabAdapter=new FollowTabAdapter(R.layout.fragment_broesw_follow_tab);
       return followTabAdapter;
    }

    @Override
    public void onDestroy() {
        RelationEvent event = new RelationEvent(2);
        LiveDataBus.get().with(Constant.RELATION_FOLLOW).postValue(event);
        super.onDestroy();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void initBase() {
        super.initBase();
        refreshLayout.setRefreshing(true);
        data = new ArrayList<>();
        relationViewModel = new ViewModelProvider(this).get(RelationViewModel.class);

        mViewModel.setShowDialog(false);
        mViewModel.getUserRelations(RelationType.ATTENTION).observe(this, userRelations -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(userRelations);
            followTabAdapter.setList(data);
        });

        followTabAdapter.addChildClickViewIds(R.id.tab_iv_Avatar,R.id.tab_iv_follow);
        followTabAdapter.setOnItemChildClickListener((adapter, view, position)-> {
            Object ob=adapter.getItem(position);
            if(!(ob instanceof UserRelations)){
                return;
            }
            attentionPosition = position;
            UserRelations userRelations=(UserRelations) ob;
            int id = view.getId();
            if(id == R.id.tab_iv_follow){
                attention(userRelations,RelationType.ATTENTION.getRelationType());
            }else if (id == R.id.tab_iv_Avatar){
                AnchorInfoActivity.launcher(userRelations.getTargetUserId(), userRelations.getSex());
            }
        });

        relationViewModel.observerRelation().observe(this, relation -> {
            followTabAdapter.removeAt(attentionPosition);
        });
    }

    private void attention(UserRelations userRelations, int RelationType) {
        if (userRelations == null)
            return;
        this.attentionAnchor= userRelations;
        User user = SpManager.getInstance().getCurrentUser();
        RelationBody body = new RelationBody(RelationType,
                user.getId(), userRelations.getTargetUserId());
            relationViewModel.delAttention(body);
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}