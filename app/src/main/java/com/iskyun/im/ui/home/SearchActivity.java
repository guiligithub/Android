package com.iskyun.im.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.R;
import com.iskyun.im.adapter.SearchTabAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.ActivitySearchBinding;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity<ActivitySearchBinding, AnchorViewModel> {
    private Anchor mAnchor;
    private SearchTabAdapter searchTabAdapter;
    private List<Anchor> data=new ArrayList<>();
    private int attentionPosition;
    @Override
    public AnchorViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AnchorViewModel.class);
    }

    @Override
    public ActivitySearchBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySearchBinding.inflate(inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_header))
                .statusBarDarkFont(true,0.2f)
                .init();
    }

    @Override
    public void initBase() {
        //setDisplay();


        if(user != null){
            if(user.getSex()== Sex.WOMAN.ordinal())
                mViewBinding.searchEdit.setHint(getString(R.string.search_man));
            else
                mViewBinding.searchEdit.setHint(getString(R.string.search_woman));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mViewBinding.searchRecyclerview.setLayoutManager(layoutManager);
        mViewBinding.searchRecyclerview.setNestedScrollingEnabled(true);
        mViewBinding.searchRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        searchTabAdapter = new SearchTabAdapter(R.layout.view_item_search);
        mViewBinding.searchRecyclerview.setAdapter(searchTabAdapter);

        searchTabAdapter.addChildClickViewIds(R.id.tab_iv_Avatar);
        searchTabAdapter.setOnItemChildClickListener((adapter, view, position)-> {
            Object ob=adapter.getItem(position);
            if(!(ob instanceof Anchor)){
                return;
            }
            attentionPosition = position;
            AnchorInfoActivity.launcher(mAnchor.getId(), mAnchor.getSex());
            finish();
        });

        mViewBinding.tabIvBack.setOnClickListener(this::onClick);
        mViewBinding.tabIvSearch.setOnClickListener(this::onClick);
        mViewBinding.tabIvDel.setOnClickListener(this::onClick);
        mViewBinding.searchEdit.addTextChangedListener(textWatcher);
        mViewBinding.searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    search();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tab_iv_search:
                search();
                break;
            case R.id.tab_iv_back:
                finish();
                break;
            case R.id.tab_iv_del:
                mViewBinding.searchEdit.setText("");
                break;
        }
    }

    //设置窗口大小
    private void setDisplay() {
        Window window =  getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity= Gravity.TOP;
        window.setAttributes(lp);

    }

    private void search(){
        String content = mViewBinding.searchEdit.getText().toString();
        if (!TextUtils.isEmpty(content)){
            //hideSoftInput();
            try {
                mViewModel.searchAnchorDetailById(Integer.parseInt(content)).observe(this, anchor -> {
                    if(anchor != null){
                        mAnchor = anchor;
                        data.clear();
                        data.addAll(Collections.singleton(anchor));
                        searchTabAdapter.setList(data);
                    } else {
                        User user = SpManager.getInstance().getCurrentUser();
                        if(user.getSex() == Sex.WOMAN.ordinal()){
                            ToastUtils.showToast(R.string.not_look_for_man);
                        }else {
                            ToastUtils.showToast(R.string.not_look_for_woman);
                        }
                    }
                });
            }catch (NumberFormatException e){
                ToastUtils.showToast(R.string.input_error);
                LogUtils.e(e.getMessage());
            }
        }else {
            ToastUtils.showToast("请输入用户ID");
        }

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mViewBinding.searchEdit.getEditableText().length() >= 1){
                mViewBinding.tabIvDel.setVisibility(View.VISIBLE);
            } else{
                mViewBinding.tabIvDel.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}
