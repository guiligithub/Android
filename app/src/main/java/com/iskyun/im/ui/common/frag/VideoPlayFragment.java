package com.iskyun.im.ui.common.frag;

import android.app.Service;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.danikula.videocache.HttpProxyCacheServer;
import com.hyphenate.easeui.player.EasyVideoCallback;
import com.hyphenate.easeui.player.EasyVideoPlayer;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.databinding.FragmentVideoPlayBinding;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;

public class VideoPlayFragment extends BaseFragment<FragmentVideoPlayBinding, LoginViewModel>
        implements EasyVideoCallback {

    public static final String TAG = "VideoPlayFragment";
    public static final int FROM_ANCHOR_DETAIL = 0x01;
    public static final int FROM_OTHER = 0x02;
    public static final String FROM = "FROM";
    public static final String PATH = "PATH";


    private EasyVideoPlayer evpPlayer;
    private Uri uri;
    private int from;

    private AudioManager audioManager;

    public VideoPlayFragment() {
    }

    public static VideoPlayFragment newInstance(int from, String videoUrl) {
        VideoPlayFragment fragment = new VideoPlayFragment();
        Bundle args = new Bundle();
        args.putInt(FROM, from);
        args.putString(PATH, videoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager) ImLiveApp.get().getSystemService(Service.AUDIO_SERVICE);
    }

    @Override
    public LoginViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public FragmentVideoPlayBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentVideoPlayBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        if (getArguments() == null) {
            return;
        }

        from = getArguments().getInt(FROM);
        String path = getArguments().getString(PATH);

        evpPlayer = mViewBinding.evpPlayer;
        evpPlayer.setCallback(this);
        evpPlayer.disableControls();
        evpPlayer.setAutoFullscreen(true);
        evpPlayer.setLoop(true);
        evpPlayer.setAutoPlay(true);

        String proxyUrl = path;
        if(path.startsWith("https://")|| path.startsWith("http://")){
            HttpProxyCacheServer proxy = ImLiveApp.getProxy();
            proxyUrl = proxy.getProxyUrl(path);
        }
        if (!TextUtils.isEmpty(proxyUrl)) {
            uri = Uri.parse(proxyUrl);
        }
        if (uri != null) {
            evpPlayer.setSource(uri);
        }
        if (from == FROM_ANCHOR_DETAIL) {
            mViewBinding.ivVoice.setVisibility(View.VISIBLE);
        }

        toggleVoice(false);
        mViewBinding.ivVoice.setOnClickListener(v -> toggleVoice(true));

    }

    private void toggleVoice(boolean clicked) {
        boolean opened = SpManager.getInstance().getVoiceOpened();
        if (clicked) {
            opened = !opened;
            SpManager.getInstance().setVoiceOpened(opened);
        }
        mViewBinding.ivVoice.setImageResource(opened ? R.mipmap.icon_video_volume_open
                : R.mipmap.icon_video_volume_close);

        if (clicked)
            toggleVoice();
    }

    /**
     * 打开，关闭 切换
     */
    public void toggleVoice() {
        boolean opened = SpManager.getInstance().getVoiceOpened();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, !opened);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (evpPlayer != null && !evpPlayer.isPlaying()) {
            evpPlayer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (evpPlayer != null) {
            evpPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
        if (evpPlayer != null) {
            evpPlayer.release();
            evpPlayer = null;
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        if (from == FROM_ANCHOR_DETAIL) {
            toggleVoice();
        }
        mViewBinding.viewLoading.setVisibility(View.GONE);
    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {

    }
}