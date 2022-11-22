package com.iskyun.im.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.net.impl.OnResourceParseCallback;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.databinding.ActivityBaseBinding;
import com.iskyun.im.databinding.ViewLayoutHeaderBinding;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.widget.GridItemDecoration;

;

public abstract class BaseActivity<VB extends ViewBinding, VM extends ViewModel> extends AppCompatActivity implements IBase<VB, VM> {

    protected VB mViewBinding;
    protected VM mViewModel;
    protected TextView mTvTitle, mTvOther;
    protected ImageView ivAction;
    private View headerView;
    protected User user;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityBaseBinding mBaseBinding = ActivityBaseBinding.inflate(getLayoutInflater());
        super.setContentView(mBaseBinding.getRoot());
        mViewBinding = onCreateViewBinding(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
    }

    @Override
    public void setContentView(View view) {
        LinearLayout rootView = findViewById(R.id.root);
        if (rootView == null) {
            return;
        }
        if (getToolbarHeader() == ToolbarHeader.CommonHeader) {
            headerView = ViewLayoutHeaderBinding.inflate(getLayoutInflater()).getRoot();
            rootView.addView(headerView);
        }
        rootView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        createViewModel();
        initHeader();
        user = SpManager.getInstance().getCurrentUser();
        initBase();
    }

    private void initHeader() {
        ToolbarHeader header = getToolbarHeader();
        switch (header) {
            case CommonHeader:
                Toolbar commonToolbar = headerView.findViewById(R.id.tl_header);
                initCommonHeader(commonToolbar);
                break;
            case TabHeader:
                Toolbar tabToolbar = mViewBinding.getRoot().findViewById(R.id.tl_tab_header);
                initTabHeader(tabToolbar);
                break;
            case CustomHeader:
                ImmersionBar.with(this).statusBarDarkFont(true).init();
                break;
        }
    }

    private void initCommonHeader(Toolbar toolbar) {
        initToolbar(toolbar);
        ImageView ivBack = headerView.findViewById(R.id.tl_back);
        mTvTitle = headerView.findViewById(R.id.tl_title);
        mTvOther = headerView.findViewById(R.id.tl_tv_other);
        if (getTitleText() != 0) {
            mTvTitle.setText(getTitleText());
        }
        ivBack.setOnClickListener(v -> {
            finish();
        });

        ivAction = headerView.findViewById(R.id.tl_other);
        ivAction.setOnClickListener(this::onClick);
        ivAction.setVisibility(View.INVISIBLE);
    }

    private void initTabHeader(Toolbar toolbar) {
        initToolbar(toolbar);
        ImageView ivBack = mViewBinding.getRoot().findViewById(R.id.tab_iv_back);
        ivBack.setOnClickListener(v -> {
            finish();
        });
        ivBack.setVisibility(View.VISIBLE);
        ivAction = mViewBinding.getRoot().findViewById(R.id.tab_iv_action);
        ivAction.setOnClickListener(this::onClick);
        ivAction.setVisibility(View.INVISIBLE);
    }

    protected void initToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ImmersionBar.with(this).titleBar(toolbar)
                .statusBarDarkFont(true)
                .keyboardEnable(true)
                .init();
    }

    protected void initRv(RecyclerView recycler){
        if (recycler != null) {
            RecyclerView.LayoutManager layoutManager = recycler.getLayoutManager();
            RecyclerView.ItemDecoration decoration;
            if (layoutManager instanceof GridLayoutManager ||
                    layoutManager instanceof StaggeredGridLayoutManager) {
                decoration = new GridItemDecoration(this,
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

    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CommonHeader;
    }

    protected void setActionImage(@DrawableRes int resId) {
        ivAction.setImageResource(resId);
        ivAction.setVisibility(View.VISIBLE);
    }

    //action 事件
    protected void onClick(View view) {

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

    /**
     * Im解析Resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (response == null) {
            return;
        }
        if (response.status == Resource.Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        } else if (response.status == Resource.Status.ERROR) {
            callback.hideLoading();
            if (!callback.hideErrorMsg) {
                showToast(response.getMessage());
            }
            callback.onError(response.errorCode, response.getMessage());
        } else if (response.status == Resource.Status.LOADING) {
            callback.onLoading(response.data);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白区域 隐藏软键盘
        if(this.getCurrentFocus() != null){
            hideSoftInput();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null&&getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS | InputMethodManager.SHOW_FORCED);
            }
        }
    }

    @Override
    public void finish() {
        hideSoftInput();
        super.finish();
    }

    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    public enum ToolbarHeader {
        CommonHeader,
        TabHeader,
        CustomHeader;
    }
}
