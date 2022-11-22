package com.iskyun.im.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityVideoPlayBinding;
import com.iskyun.im.ui.common.frag.VideoPlayFragment;
import com.iskyun.im.utils.ActivityUtils;

/**
 * 视频播放
 */
public class VideoPlayActivity extends BaseActivity<ActivityVideoPlayBinding, ViewModel> {


    private String path;

    public static void launcherVideoPlay(Activity activity, String path){
        Bundle args = new Bundle();
        args.putString(VideoPlayFragment.PATH, path);
        ActivityUtils.launcher(activity, args, VideoPlayActivity.class);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(VideoPlayFragment.PATH, path);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        path = savedInstanceState.getString(VideoPlayFragment.PATH);
    }

    @Override
    public ViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityVideoPlayBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityVideoPlayBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            path = getIntent().getExtras().getString(VideoPlayFragment.PATH);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        VideoPlayFragment videoPlayFragment = (VideoPlayFragment) fm.findFragmentByTag(VideoPlayFragment.TAG);
        if (videoPlayFragment == null) {
            videoPlayFragment = VideoPlayFragment.newInstance(
                    VideoPlayFragment.FROM_OTHER, path);
        } else {
            Bundle args = new Bundle();
            args.putInt(VideoPlayFragment.FROM, VideoPlayFragment.FROM_OTHER);
            args.putString(VideoPlayFragment.PATH, path);
            videoPlayFragment.setArguments(args);
        }
        ft.replace(R.id.container, videoPlayFragment, VideoPlayFragment.TAG);
        ft.commit();
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}