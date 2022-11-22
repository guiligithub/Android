package com.iskyun.im.ui.mine.frag;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.FansTabAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.databinding.FragmentBroeswFansBinding;
import com.iskyun.im.databinding.FragmentBroeswFansItemBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.home.AnchorInfoActivity;
import com.iskyun.im.ui.mine.viewmodel.UserRelationsModel;
import com.iskyun.im.utils.event.RelationEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.RelationViewModel;

import java.util.ArrayList;
import java.util.List;

public class FansFragment extends BaseListFragment<FragmentBroeswFansBinding, UserRelationsModel> {
    //自定义recyclerveiw的适配器
    private FansTabAdapter fansTabAdapter;
    private List<UserRelations> data;
    private RelationViewModel relationViewModel;
    private int attentionPosition;
    private UserRelations attentionAnchor;

    public FansFragment() {
    }

    @Override
    public UserRelationsModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(UserRelationsModel.class);
    }

    @Override
    public FragmentBroeswFansBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBroeswFansBinding.inflate(inflater);
    }

    @Override
    public void onDestroy() {
        RelationEvent event = new RelationEvent(1);
        LiveDataBus.get().with(Constant.RELATION_FANS).postValue(event);
        super.onDestroy();
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    protected BaseQuickAdapter<UserRelations, BaseDataBindingHolder<FragmentBroeswFansItemBinding>> onCreateAdapter() {
        fansTabAdapter=new FansTabAdapter(R.layout.fragment_broesw_fans_item);
        return fansTabAdapter;
    }

    @Override
    public void initBase() {
        super.initBase();
        data = new ArrayList<>();
        relationViewModel = new ViewModelProvider(this).get(RelationViewModel.class);
        mViewModel.getUserRelations(RelationType.BLOCK).observe(this,userRelations -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(userRelations);
            fansTabAdapter.setList(data);
        });
        fansTabAdapter.addChildClickViewIds(R.id.tab_iv_Avatar,R.id.tab_iv_follow);
        fansTabAdapter.setOnItemChildClickListener((adapter, view, position)-> {
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
                AnchorInfoActivity.launcher(userRelations.getStartUserId(), userRelations.getSex());
            }
        });

        relationViewModel.observerRelation().observe(this, relation -> {
            boolean isAttention = attentionAnchor.isAttention();
            for (UserRelations userRelations : fansTabAdapter.getData()){
                if(userRelations.getStartUserId() == attentionAnchor.getStartUserId()){
                    userRelations.setAttention(!isAttention);
                    fansTabAdapter.notifyItemChanged(fansTabAdapter.getItemPosition(userRelations));
                }
            }
        });

    }

    private void attention(UserRelations userRelations, int RelationType) {
        if (userRelations == null)
            return;
        this.attentionAnchor= userRelations;
        User user = SpManager.getInstance().getCurrentUser();
        RelationBody body = new RelationBody(RelationType,
                user.getId(), userRelations.getStartUserId());
        if (userRelations.isAttention()) {
            relationViewModel.delAttention(body);
        } else {
            relationViewModel.addUserRelation(body);
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}