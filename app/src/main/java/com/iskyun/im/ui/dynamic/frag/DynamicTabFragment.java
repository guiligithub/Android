package com.iskyun.im.ui.dynamic.frag;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.hyphenate.easeui.constants.EaseConstant;
import com.iskyun.im.R;
import com.iskyun.im.adapter.DynamicTabAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.Dynamic;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.InteractionBody;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.databinding.FragmentDynamicTabBinding;
import com.iskyun.im.databinding.ViewItemDynamicTabBinding;
import com.iskyun.im.ui.dynamic.viewmodel.DynamicViewModel;
import com.iskyun.im.ui.dynamic.ReleaseDynamicsActivity;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.ui.home.AnchorInfoActivity;
import com.iskyun.im.ui.message.ChatActivity;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.RelationViewModel;

import java.util.ArrayList;
import java.util.List;

public class DynamicTabFragment extends BaseListFragment<FragmentDynamicTabBinding, DynamicViewModel> {
    public static final int ALL_DYNAMIC = 0x100;
    public static final int MINE_DYNAMIC = 0x101;
    public static final int PERSONAL_DYNAMIC = 0x102;
    public static final String DYNAMIC = "DYNAMIC";
    public static final String USERID = "USERID";
    private DynamicTabAdapter dynamicTabAdapter;
    private RelationViewModel relationViewModel;
    private List<Dynamic> data;
    private Dynamic attentionDynamic;
    private int attentionPosition;
    private int type;
    private int userid;

    public DynamicTabFragment() {
    }

    public static DynamicTabFragment newInstance(int dynamicType,int userId) {
        DynamicTabFragment fragment = new DynamicTabFragment();
        Bundle args = new Bundle();
        args.putInt(DYNAMIC, dynamicType);
        args.putInt(USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public FragmentDynamicTabBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentDynamicTabBinding.inflate(inflater);
    }

    @Override
    protected BaseQuickAdapter<Dynamic, BaseDataBindingHolder<ViewItemDynamicTabBinding>> onCreateAdapter() {
        dynamicTabAdapter = new DynamicTabAdapter();
        return dynamicTabAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReleaseDynamicsActivity.DYNAMIC_REQ) {
            onRefresh();
        }
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    public DynamicViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(DynamicViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(DYNAMIC);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(DYNAMIC, type);
    }

    @Override
    public void initBase() {
        super.initBase();
        SimpleItemAnimator animator = (SimpleItemAnimator) recycler.getItemAnimator();
        if(animator != null)
            animator.setSupportsChangeAnimations(false);

        if (getArguments() != null){
            type = getArguments().getInt(DYNAMIC);
            userid = getArguments().getInt(USERID);
        }
        relationViewModel = new ViewModelProvider(this).get(RelationViewModel.class);
        data = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        mViewModel.getDynamics(type,userid).observe(this, dynamics -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(dynamics);
            dynamicTabAdapter.setList(data);
        });

        dynamicTabAdapter.addChildClickViewIds(R.id.tab_tv_like, R.id.tab_iv_Avatar,
                R.id.tab_iv_Accost, R.id.tab_iv_follow, R.id.tab_iv_more);
        dynamicTabAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Object ob = adapter.getItem(position);
            if (!(ob instanceof Dynamic)) {
                return;
            }
            onItemClick((Dynamic) ob,view.getId(), position);
        });

        relationViewModel.observerRelation().observe(this, relation -> {
            boolean isAttention = attentionDynamic.isAttention();

            //更新所有
            for (Dynamic dynamic : dynamicTabAdapter.getData()){
                if(dynamic.getUserId() == attentionDynamic.getUserId()){
                    dynamic.setAttention(!isAttention);
                    dynamicTabAdapter.notifyItemChanged(dynamicTabAdapter.getItemPosition(dynamic));
                }
            }
        });

        mViewModel.observerInteraction().observe(this, s -> {
            boolean isCommend = attentionDynamic.isCommend();
            if(isCommend){
                attentionDynamic.setCommendNum(attentionDynamic.getCommendNum()-1);
            }else {
                attentionDynamic.setCommendNum(attentionDynamic.getCommendNum()+1);
            }
            attentionDynamic.setCommend(!isCommend);
            dynamicTabAdapter.notifyItemChanged(attentionPosition);
        });
    }

    private void onItemClick(Dynamic dynamic,int id, int position){
        attentionPosition = position;
        this.attentionDynamic = dynamic;
        if (id == R.id.tab_tv_like) {
            interaction(dynamic.getId());
        } else if (id == R.id.tab_iv_Accost) {
            accost(dynamic.getUserId());
        } else if (id == R.id.tab_iv_follow) {
            attention(dynamic);
        } else if (id == R.id.tab_iv_more) {
            //moreAction();
        } else if (id == R.id.tab_iv_Avatar) {
            AnchorInfoActivity.launcher(dynamic.getUserId(), dynamic.getSex());
        }
    }

    /**
     * 点赞
     * @param dynamicId
     */
    private void interaction(String dynamicId) {
        InteractionBody body = new InteractionBody(1, dynamicId);
        mViewModel.userInteraction(body);
    }


    /**
     * 关注
     *
     * @param dynamic
     */
    private void attention(Dynamic dynamic) {
        if (dynamic == null)
            return;
        User user = SpManager.getInstance().getCurrentUser();
        RelationBody body = new RelationBody(RelationType.ATTENTION.getRelationType(),
                user.getId(), dynamic.getUserId());
        if (dynamic.isAttention()) {
            relationViewModel.delAttention(body);
        } else {
            relationViewModel.addUserRelation(body);
        }
    }

    /**
     * 搭讪
     * @param userId
     */
    private void accost(int userId) {
        ChatActivity.actionStart(getActivity(), ""+userId,
                EaseConstant.CHATTYPE_SINGLE);
    }

    /**
     * 更多
     */
    private void moreAction() {
        MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_DYNAMIC);
        fragment.showNow(getChildFragmentManager(), "attention");
        fragment.setOnItemClickListener(type -> {

        });
    }


    @Override
    public int getTitleText() {
        return 0;
    }
}