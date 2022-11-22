package com.iskyun.im.ui.frag;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.iskyun.im.R;

public class ContentDialogFragment extends  CommonDialogFragment{

    private TextView tvContent;

    @Override
    public int getMiddleLayoutId() {
        return R.layout.view_dialog_content;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvContent = findViewById(R.id.dialog_tv_content);
        if(content != null){
            tvContent.setText(content);
        }
    }

    public static class Builder extends CommonDialogFragment.Builder {

        public Builder(FragmentActivity context) {
            super(context);
        }

        @Override
        public CommonDialogFragment getFragment() {
            return new ContentDialogFragment();
        }

    }
}
