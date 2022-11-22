package com.iskyun.im.base;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.widget.AppLoadMore;
import com.iskyun.im.widget.GridItemDecoration;

public abstract class BaseListFragment<VB extends ViewBinding, VM extends ViewModel>
        extends BaseFragment<VB, VM> {

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recycler;
    protected BaseQuickAdapter<?, ?> mAdapter;
    protected BaseListViewModel<?> listViewModel;
    protected boolean isNotMoreData = false;

    @Override
    public void initBase() {
        initRecycler();
        initRefresh();
        if (loadMoreEnable()) {
            initLoadMore();
        }
        if (mViewModel instanceof BaseListViewModel) {
            listViewModel = (BaseListViewModel<?>) mViewModel;
            listViewModel.observerStatus().observe(this, this::statusChange);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycler() {
        recycler = mViewBinding.getRoot().findViewById(R.id.recycler_view);
        if (recycler != null) {
            RecyclerView.LayoutManager layoutManager = recycler.getLayoutManager();
            RecyclerView.ItemDecoration decoration;
            if (layoutManager instanceof GridLayoutManager ||
                    layoutManager instanceof StaggeredGridLayoutManager) {
                decoration = new GridItemDecoration(getActivity(),
                        getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.white);
            } else {
                DividerItemDecoration divider = new DividerItemDecoration(ImLiveApp.get(),
                        DividerItemDecoration.VERTICAL);
                divider.setDrawable(getResources().getDrawable(R.drawable.divider));
                decoration = divider;
            }
            recycler.addItemDecoration(decoration);
            recycler.setNestedScrollingEnabled(true);
            recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mAdapter = onCreateAdapter();
            recycler.setAdapter(mAdapter);
            mAdapter.setEmptyView(R.layout.view_empty);
        }
    }

    private void initRefresh() {
        refreshLayout = mViewBinding.getRoot().findViewById(R.id.swipe_refresh);
        if (refreshLayout == null)
            return;
        refreshLayout.setEnabled(refreshEnable());
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(ImLiveApp.get(), R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(this::onRefresh);
    }

    private void initLoadMore() {
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this::loadMore);
        mAdapter.getLoadMoreModule().setAutoLoadMore(true);
        mAdapter.getLoadMoreModule().setLoadMoreView(new AppLoadMore());
        //当自动加载开启，同时数据不满一屏时，是否继续执行自动加载更多
        mAdapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(true);
    }

    protected abstract BaseQuickAdapter<?, ?> onCreateAdapter();

    /**
     * 是否可刷新
     *
     * @return
     */
    protected abstract boolean refreshEnable();

    protected boolean loadMoreEnable() {
        return true;
    }

    /**
     * 状态监听
     *
     * @param status
     */
    protected void statusChange(BaseListViewModel.Status status) {
        if (refreshLayout == null)
            return;
        switch (status) {
            case ON_REFRESHING:
                break;
            case ON_REFRESH:
                LogUtils.e("ON_REFRESH");
                isNotMoreData = false;
                refreshLayout.setRefreshing(true);
                if(loadMoreEnable())
                    mAdapter.getLoadMoreModule().setEnableLoadMore(true);
                break;
            case ON_LOAD_MORE:
                LogUtils.e("ON_LOAD_MORE");
                if(loadMoreEnable())
                    mAdapter.getLoadMoreModule().setEnableLoadMore(true);
                break;
            case ON_COMPLETED:
                LogUtils.e("ON_COMPLETED");
                refreshLayout.setRefreshing(false);
                if(loadMoreEnable()){
                    mAdapter.getLoadMoreModule().setEnableLoadMore(true);
                    mAdapter.getLoadMoreModule().loadMoreComplete();
                }
                break;
            case ON_NO_MORE:
                LogUtils.e("ON_NO_MORE");
                isNotMoreData = true;
                refreshLayout.setRefreshing(false);
                if(loadMoreEnable()){
                    mAdapter.getLoadMoreModule().setEnableLoadMore(true);
                    mAdapter.getLoadMoreModule().loadMoreEnd();
                }
                break;
            case ON_FAIL:
                refreshLayout.setRefreshing(false);
                break;
            default:
                break;
        }
    }

    protected void loadMore() {
//        if(!isNotMoreData)
            listViewModel.loadData(BaseListViewModel.Status.ON_LOAD_MORE);
    }

    protected void onRefresh() {
        if(loadMoreEnable())
            mAdapter.getLoadMoreModule().setEnableLoadMore(false);
        listViewModel.loadData(BaseListViewModel.Status.ON_REFRESH);
    }


}
