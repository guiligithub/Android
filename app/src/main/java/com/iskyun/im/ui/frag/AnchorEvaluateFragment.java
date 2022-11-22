package com.iskyun.im.ui.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.databinding.FragmentEvaluateBinding;
import com.iskyun.im.widget.RatingStarView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主播评价
 */
public class AnchorEvaluateFragment extends BaseDialogFragment<FragmentEvaluateBinding> {

    private static final String RECORD_ID = "recordId";
    private static final String USER_ID = "userId";
    private static final String NICKNAME = "nickName";
    private boolean autoEvaluate = true;
    private String recordId;
    private int userId;

    private AnchorEvaluateFragment() {
    }

    public static AnchorEvaluateFragment newInstance(String recordId, int userId, String nickName) {
        AnchorEvaluateFragment fragment = new AnchorEvaluateFragment();
        Bundle args = new Bundle();
        args.putString(RECORD_ID, recordId);
        args.putString(NICKNAME, nickName);
        args.putInt(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public FragmentEvaluateBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentEvaluateBinding.inflate(inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogPaddingParams();
        //setCancelable(false);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        RatingStarView rsv = mViewBinding.evaluateRsvStar;
        rsv.setOnClickListener(v -> {
            if (rsv.getRating() == 0) {
                rsv.setRating(1);
            }
        });

        mViewBinding.evaluateBtn.setOnClickListener(v -> {
            autoEvaluate = false;
            addEvaluate((int) mViewBinding.evaluateRsvStar.getRating());
            dismiss();
        });

    }

    @Override
    public void initData() {
        super.initData();

        if (getArguments() == null) {
            return;
        }
        recordId = getArguments().getString(RECORD_ID);
        userId = getArguments().getInt(USER_ID);
        String nickName = getArguments().getString(NICKNAME);

        String title = String.format(getString(R.string.evaluate_title), nickName);
        mViewBinding.evaluateTvTitle.setText(title);
    }

    @Override
    public void dismiss() {
        if(autoEvaluate){
            addEvaluate(5);
        }
        super.dismiss();
    }

    private void addEvaluate(int score) {
        if(TextUtils.isEmpty(recordId)){
            return;
        }
        UserRepository.get().addEvaluate(recordId, userId, score).enqueue(new Callback<AppResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<AppResponse<String>> call, @NonNull Response<AppResponse<String>> response) {

            }

            @Override
            public void onFailure(@NonNull Call<AppResponse<String>> call, Throwable t) {

            }
        });
    }
}
