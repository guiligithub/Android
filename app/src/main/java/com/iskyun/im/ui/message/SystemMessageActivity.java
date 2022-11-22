package com.iskyun.im.ui.message;

import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.R;
import com.iskyun.im.adapter.SystemMessageAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.bean.SysMessage;
import com.iskyun.im.databinding.ActivitySystemMessageBinding;
import com.iskyun.im.ui.message.viewmodel.SystemMessageViewModel;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class SystemMessageActivity extends BaseActivity<ActivitySystemMessageBinding, SystemMessageViewModel>{
    private SystemMessageAdapter systemMessageAdapter;
    private List<SysMessage> data=new ArrayList<>();
    @Override
    public SystemMessageViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(SystemMessageViewModel.class);
    }

    @Override
    public ActivitySystemMessageBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySystemMessageBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        RecyclerView.ItemDecoration decoration = new GridItemDecoration(this,
                getResources().getDimensionPixelSize(R.dimen.text_margin), R.color.white);
        mViewBinding.recyclerView.setLayoutManager(layoutManager);
        mViewBinding.recyclerView.addItemDecoration(decoration);
        mViewBinding.recyclerView.setNestedScrollingEnabled(true);
        mViewBinding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        systemMessageAdapter = new SystemMessageAdapter(R.layout.view_item_system_message);
        mViewBinding.recyclerView.setAdapter(systemMessageAdapter);

        mViewModel.getSysMessage().observe(this, sysMessage -> {
            data.addAll(sysMessage);
            systemMessageAdapter.setList(data);
        });
    }

    @Override
    public int getTitleText() {
        return R.string.system_message;
    }
}
