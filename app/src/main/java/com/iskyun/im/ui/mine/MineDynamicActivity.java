package com.iskyun.im.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.databinding.ActivityMineDynamicBinding;
import com.iskyun.im.ui.dynamic.frag.DynamicTabFragment;
import com.iskyun.im.ui.dynamic.ReleaseDynamicsActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.manager.SpManager;

public class MineDynamicActivity extends BaseActivity<ActivityMineDynamicBinding, ViewModel> {
    private int TitleText;

    @Override
    public ViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityMineDynamicBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityMineDynamicBinding.inflate(inflater);
    }

    /**
     * MineDynamicActivity 启动
     * @param userId
     */
    public static void launcher(int userId) {
        Bundle args = new Bundle();
        args.putInt(Constant.ANCHOR_ID, userId);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(),args, MineDynamicActivity.class);
    }

    @Override
    public void initBase() {
        ivAction.setImageResource(R.mipmap.icon_camera);
        ivAction.setVisibility(View.VISIBLE);
        if (getIntent().getExtras() != null) {
            int userId = getIntent().getExtras().getInt(Constant.ANCHOR_ID);
            DynamicTabFragment fragment = DynamicTabFragment.newInstance(DynamicTabFragment.PERSONAL_DYNAMIC,userId);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, String.valueOf(userId)).commit();
            ivAction.setVisibility(View.GONE);
        }else if (getSupportFragmentManager().findFragmentByTag("mine") == null) {
            int userId = SpManager.getInstance().getCurrentUser().getId();
            DynamicTabFragment fragment = DynamicTabFragment.newInstance(DynamicTabFragment.MINE_DYNAMIC,userId);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, "mine").commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
           DynamicTabFragment fragment = (DynamicTabFragment) getSupportFragmentManager().findFragmentByTag("mine");
           if(fragment != null){
               fragment.onActivityResult(requestCode,resultCode,data);
           }
        }
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        Bundle args = new Bundle();
        args.putInt(DynamicTabFragment.DYNAMIC, DynamicTabFragment.MINE_DYNAMIC);
        ActivityUtils.launcherForResult(this, ReleaseDynamicsActivity.DYNAMIC_REQ, args,
                ReleaseDynamicsActivity.class);
    }

    @Override
    public int getTitleText() {
        if (getIntent().getExtras() != null) {
            TitleText=R.string.his_dynamic;
        }else {
            TitleText=R.string.mine_dynamic;
        }
        return TitleText;
    }
}
