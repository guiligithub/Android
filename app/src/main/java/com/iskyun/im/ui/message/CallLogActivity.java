package com.iskyun.im.ui.message;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityListBinding;
import com.iskyun.im.ui.message.frag.CallLogFragment;
import com.iskyun.im.ui.message.viewmodel.CallLogViewModel;

public class CallLogActivity extends BaseActivity<ActivityListBinding, CallLogViewModel> {

    @Override
    public CallLogViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(CallLogViewModel.class);
    }

    @Override
    public ActivityListBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityListBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        CallLogFragment fragment = new CallLogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, "callLog");
        ft.commit();
    }

    @Override
    public int getTitleText() {
        return R.string.call_record;
    }
}
