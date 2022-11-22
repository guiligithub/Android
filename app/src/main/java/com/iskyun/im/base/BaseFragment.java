package com.iskyun.im.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.widget.GridItemDecoration;

public abstract class BaseFragment<VB extends ViewBinding, VM extends ViewModel> extends Fragment implements IBase<VB, VM> {

    protected VB mViewBinding;
    protected VM mViewModel;
    protected View statusBarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = onCreateViewBinding(inflater);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createViewModel();
        fitsLayoutOverlap();
        initBase();
    }

    protected <T extends ViewModel> T onCreateViewModel(Class<T> tClass) {
        ViewModelProvider provider = new ViewModelProvider(this);
        return provider.get(tClass);
    }

    private VM createViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        mViewModel = onCreateViewModel(provider);
        return mViewModel;
    }

    private void fitsLayoutOverlap() {
        if (statusBarView != null) {
            ImmersionBar.setStatusBarView(this, statusBarView);
        } else {
            ImmersionBar.setTitleBar(this, mViewBinding.getRoot().findViewById(R.id.tl_header));
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fitsLayoutOverlap();
    }

    protected void initRv(RecyclerView recycler){
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
        }
    }
}
