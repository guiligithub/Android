package com.hyphenate.easecallkit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.hyphenate.easecallkit.R;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easecallkit.widget.EaseImageView;
import com.hyphenate.util.EMLog;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/15/2021
 */
public class EaseCommingCallView extends FrameLayout {

    private static final String TAG = EaseVideoCallActivity.class.getSimpleName();

    private ImageButton mBtnReject;
    private ImageButton mBtnPickup;
    private TextView mInviterName;
    private OnActionListener mOnActionListener;
    private EaseImageView avatar_view;
    private Bitmap headBitMap;
    private String headUrl;

    public EaseCommingCallView(@NonNull Context context) {
        this(context, null);
    }

    public EaseCommingCallView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseCommingCallView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_comming_call, this);
        mBtnReject = findViewById(R.id.btn_reject);
        mBtnPickup = findViewById(R.id.btn_pickup);
        mInviterName = findViewById(R.id.tv_nick);
        avatar_view = findViewById(R.id.iv_avatar);
        mBtnReject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnActionListener != null) {
                    mOnActionListener.onRejectClick(v);
                }
            }
        });

        mBtnPickup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnActionListener != null) {
                    mOnActionListener.onPickupClick(v);
                }
            }
        });
    }

    public void setInviteInfo(String username){
        mInviterName.setText(EaseCallKitUtils.getUserNickName(username));
        headUrl = EaseCallKitUtils.getUserHeadImage(username);

        //??????????????????
        loadHeadImage();
    }

    /**
     * ????????????????????????
     * @return
     */
    private void loadHeadImage() {
        if(headUrl != null) {
            if (headUrl.startsWith("http://") || headUrl.startsWith("https://")) {
                new AsyncTask<String, Void, Bitmap>() {
                    //?????????????????????????????????????????????????????????????????????UI???UI??????????????????
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap bitmap = null;
                        FutureTarget<Bitmap> futureTarget =
                                Glide.with(getContext())
                                        .asBitmap()
                                        .load(headUrl)
                                        .submit(500, 500);
                        try {
                            bitmap = futureTarget.get();
                        }catch (Exception e){
                            e.getStackTrace();
                        }
                        return  bitmap;
                    }

                    //???doInBackground ??????????????????onPostExecute ????????????UI ???????????????
                    // ????????????????????????????????????????????????UI??????????????????????????????????????????.
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null && !bitmap.isRecycled()) {
                            avatar_view.setImageBitmap(bitmap);
                        }
                    }
                }.execute(headUrl);
            } else {
                if(headBitMap == null){
                    //????????????????????????????????????????????????????????????????????????????????????Bitmap??????
                    headBitMap = BitmapFactory.decodeFile(headUrl);
                }
                if(headBitMap != null && !headBitMap.isRecycled()){
                    avatar_view.setImageBitmap(headBitMap);
                }else{
                    EMLog.d(TAG,"headBitMap is isRecycled");
                }
            }
        }
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }



    public void setOnActionListener(OnActionListener listener) {
        this.mOnActionListener = listener;
    }

    public interface OnActionListener {
        void onRejectClick(View v);
        void onPickupClick(View v);
    }


    float[] getScreenInfo(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float[] info = new float[5];
        if(manager != null) {
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            info[0] = dm.widthPixels;
            info[1] = dm.heightPixels;
            info[2] = dm.densityDpi;
            info[3] = dm.density;
            info[4] = dm.scaledDensity;
        }
        return info;
    }
}

