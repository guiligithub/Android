package com.iskyun.im.ui.auth;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityInvitationCodeBinding;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.ui.auth.viewomodel.AuthViewModel;

public class InvitationCodeActivity extends BaseActivity<ActivityInvitationCodeBinding, AuthViewModel> {

    @Override
    public AuthViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AuthViewModel.class);
    }

    @Override
    public ActivityInvitationCodeBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityInvitationCodeBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mViewBinding.invitationCodeSubmit.setOnClickListener(this::onClick);
        if(getIntent() != null && getIntent().getExtras() != null){
            int code  = getIntent().getExtras().getInt(AuthActivity.AUTH_TYPE);
            if(code != 0){
                mViewBinding.invitationCodeEdit.setText(String.valueOf(code));
            }
        }
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.invitation_code_submit) {
            String code = mViewBinding.invitationCodeEdit.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                ToastUtils.showToast(R.string.input_invitation_code);
                return;
            }
            Intent data = new Intent();
            data.putExtra(AuthActivity.AUTH_TYPE, code);
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(mViewBinding.invitationCodeEdit.getWindowToken(),
                    0);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public int getTitleText() {
        return R.string.invitation_code;
    }
}