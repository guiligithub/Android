package com.iskyun.im.ui.setting.frag;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.BlacklistAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.Blacklist;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.databinding.FragmentBlacklistBinding;
import com.iskyun.im.databinding.ViewItemBlacklistBinding;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.RelationViewModel;

import java.util.ArrayList;
import java.util.List;

public class BlacklistFragment extends BaseListFragment<FragmentBlacklistBinding, RelationViewModel> {

    private List<Blacklist> mBlacklists = new ArrayList<>();
    private int mPosition;

    @Override
    protected BaseQuickAdapter<Blacklist, BaseDataBindingHolder<ViewItemBlacklistBinding>> onCreateAdapter() {
        return new BlacklistAdapter();
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    protected boolean loadMoreEnable() {
        return false;
    }

    @Override
    public RelationViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RelationViewModel.class);
    }

    @Override
    public FragmentBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        super.initBase();

        SimpleItemAnimator animator = (SimpleItemAnimator) recycler.getItemAnimator();
        if(animator != null)
            animator.setSupportsChangeAnimations(false);

        refreshLayout.setRefreshing(true);
        mViewModel.getBlacklist();
        mViewModel.observerBlacklist().observe(this, blacklists -> {
            if (mViewModel.isRefresh()) {
                mBlacklists.clear();
            }
            mBlacklists.addAll(blacklists);
            ((BlacklistAdapter) mAdapter).setList(mBlacklists);
        });

        ((BlacklistAdapter) mAdapter).addChildClickViewIds(R.id.view_item_blacklist_btn_relieve);
        ((BlacklistAdapter) mAdapter).setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Object ob = adapter.getItem(position);
                mPosition = position;
                if (ob instanceof Blacklist) {
                    removeBlacklist(((Blacklist) ob).getId());
                }
            }
        });

        mViewModel.observerRelation().observe(this, s -> ((BlacklistAdapter) mAdapter).removeAt(mPosition));
    }

    /**
     * 解除黑名单
     * @param userId
     */
    private void removeBlacklist(int userId){
        User user = SpManager.getInstance().getCurrentUser();
        if(user != null){
            RelationBody body = new RelationBody(RelationType.BLOCK.getRelationType(),
                    user.getId(), userId);
            mViewModel.setShowDialog(true);
            mViewModel.delAttention(body);
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}
