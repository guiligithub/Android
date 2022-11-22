package com.iskyun.im.ui.message.frag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.OnlineServiceAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.OnlineService;
import com.iskyun.im.databinding.FragmentBlacklistBinding;
import com.iskyun.im.databinding.ViewItemOnlineBinding;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.ui.message.viewmodel.OnlineViewModel;

import java.util.ArrayList;
import java.util.List;

public class OnlineListFragment extends BaseListFragment<FragmentBlacklistBinding, OnlineViewModel> {

    private List<OnlineService> mOnlineData = new ArrayList<>();

    @Override
    protected BaseQuickAdapter<OnlineService, BaseDataBindingHolder<ViewItemOnlineBinding>> onCreateAdapter() {
        return new OnlineServiceAdapter();
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
    public OnlineViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(OnlineViewModel.class);
    }

    @Override
    public FragmentBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        super.initBase();

        SimpleItemAnimator animator = (SimpleItemAnimator) recycler.getItemAnimator();
        if (animator != null)
            animator.setSupportsChangeAnimations(false);

        refreshLayout.setRefreshing(true);

        mViewModel.getOnlineService();

        mViewModel.observerOnline().observe(this, onlineServices -> {
            mOnlineData.clear();
            refreshLayout.setRefreshing(false);
            mOnlineData.addAll(onlineServices);
            ((OnlineServiceAdapter) mAdapter).setList(mOnlineData);
        });

        mAdapter.addChildClickViewIds(R.id.view_item_online_tv_account);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                OnlineService onlineService = (OnlineService) adapter.getData().get(position);
                copyWechat(onlineService.getAccount());
            }
        });

        /*mAdapter.setOnItemClickListener((adapter, view, position) -> {
            OnlineService onlineService = (OnlineService) adapter.getData().get(position);
            OnlineActivity.actionStart(ImLiveApp.get().getTopActivity(),
                    Constant.HX_ID + onlineService.getId());
        });*/
    }

    private void copyWechat(String content) {
        try {
            ClipboardManager cm = (ClipboardManager) getActivity()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("label", content);
            cm.setPrimaryClip(mClipData);
            ToastUtils.showToast(R.string.copy_success);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}
