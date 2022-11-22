package com.iskyun.im.ui.frag;

import com.iskyun.im.base.BaseActivity;

public class SimpleDialogFragment extends CommonDialogFragment {
    public static final String MESSAGE_KEY = "message";

    @Override
    public void initArgument() {
        if(getArguments() != null) {
            title = getArguments().getString(MESSAGE_KEY);
        }
    }

    public static class Builder extends CommonDialogFragment.Builder {

        public Builder(BaseActivity context) {
            super(context);
        }

        @Override
        protected CommonDialogFragment getFragment() {
            return new SimpleDialogFragment();
        }

    }


}

