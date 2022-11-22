package com.iskyun.im.ui.common;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.utils.Color.ColorUtils;
import com.huantansheng.easyphotos.utils.system.SystemUtils;
import com.iskyun.im.R;
import com.iskyun.im.adapter.PreviewPhotosAdapter;

import java.util.ArrayList;


/**
 * 图片预览页
 */
public class PreviewPicturesActivity extends AppCompatActivity implements PreviewPhotosAdapter.OnClickListener, View.OnClickListener {

    private ArrayList<String> photo = new ArrayList<>();

    public static void start(Activity act, int albumItemIndex, int currIndex, ArrayList<String> list) {
        Intent intent = new Intent(act, PreviewPicturesActivity.class);
        intent.putExtra(Key.PREVIEW_ALBUM_ITEM_INDEX, albumItemIndex);
        intent.putExtra(Key.PREVIEW_PHOTO_INDEX, currIndex);
        intent.putStringArrayListExtra("ListString",list);
        if(act != null)
            act.startActivityForResult(intent, Code.REQUEST_PREVIEW_ACTIVITY);
    }

    /**
     * 一些旧设备在UI小部件更新之间需要一个小延迟
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            SystemUtils.getInstance().systemUiHide(PreviewPicturesActivity.this, decorView);
        }
    };
    private RelativeLayout mBottomBar;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

        }
    };
    private boolean mVisible;
    View decorView;
    private RecyclerView rvPhotos;
    private PreviewPhotosAdapter adapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager lm;
    private int index;

    private int resultCode = RESULT_CANCELED;
    private int lastPosition = 0;//记录recyclerView最后一次角标位置，用于判断是否转换了item
    private boolean isSingle = Setting.count == 1;
    private boolean unable = Result.count() == Setting.count;

    private int statusColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        SystemUtils.getInstance().systemUiInit(this, decorView);

        setContentView(R.layout.activity_preview);

        hideActionBar();
        adaptationStatusBar();
        initData();
        initView();
    }

    private void adaptationStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusColor = ContextCompat.getColor(this, com.huantansheng.easyphotos.R.color.easy_photos_status_bar);
            if (ColorUtils.isWhiteColor(statusColor)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    private void initData() {
        Intent intent = getIntent();
        int albumItemIndex = intent.getIntExtra(Key.PREVIEW_ALBUM_ITEM_INDEX, 0);
        photo.clear();

        photo.addAll(intent.getStringArrayListExtra("ListString"));

        index = intent.getIntExtra(Key.PREVIEW_PHOTO_INDEX, 0);

        lastPosition = index;
        mVisible = true;
    }



    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideAnimation.setDuration(UI_ANIMATION_DELAY);
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

    }


    private void show() {
        if (Build.VERSION.SDK_INT >= 16) {
            SystemUtils.getInstance().systemUiShow(this, decorView);
        }

        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.post(mShowPart2Runnable);
    }

    @Override
    public void onPhotoClick() {
        toggle();
    }

    @Override
    public void onPhotoScaleChanged() {
        if (mVisible) hide();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {
        setClick(R.id.iv_back);
        if (!SystemUtils.getInstance().hasNavigationBar(this)) {
            FrameLayout mRootView = (FrameLayout) findViewById(R.id.m_root_view);
            mRootView.setFitsSystemWindows(true);
            if (ColorUtils.isWhiteColor(statusColor)) {
                SystemUtils.getInstance().setStatusDark(this, true);
            }
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        adapter = new PreviewPhotosAdapter(this, photo, this);
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(lm);
        rvPhotos.setAdapter(adapter);
        rvPhotos.scrollToPosition(index);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvPhotos);
        rvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(lm);
                if (view == null) {
                    return;
                }
                int position = lm.getPosition(view);
                if (lastPosition == position) {
                    return;
                }
                lastPosition = position;
            }
        });

    }

    private boolean clickDone = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            finish();
        }
    }

    private void setClick(@IdRes int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

}
