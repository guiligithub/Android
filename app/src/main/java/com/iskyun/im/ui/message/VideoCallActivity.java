package com.iskyun.im.ui.message;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.danikula.videocache.HttpProxyCacheServer;
import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.ui.EaseVideoCallActivity;
import com.hyphenate.easecallkit.widget.MyChronometer;
import com.hyphenate.easeui.player.EasyVideoCallback;
import com.hyphenate.easeui.player.EasyVideoPlayer;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.Consume;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.CallRecordBody;
import com.iskyun.im.data.req.ChatRecordBody;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.helper.SendMsgHelper;
import com.iskyun.im.ui.common.GifPlayActivity;
import com.iskyun.im.ui.frag.GiftFragment;
import com.iskyun.im.ui.mine.viewmodel.MineModel;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.event.AnchorEvaluateEvent;
import com.iskyun.im.utils.event.CallEndEvent;
import com.iskyun.im.utils.event.DiamondChangeEvent;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;
import com.iskyun.im.viewmodel.ConsumeViewModel;
import com.iskyun.im.viewmodel.RelationViewModel;

import java.util.HashMap;
import java.util.Map;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.TiPanelLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoCallActivity extends EaseVideoCallActivity implements EasyVideoCallback, SensorEventListener {

    private ImageView imageVoiceCover, ivAttention;
    private ImageButton ibGift, ivFaceunity;
    private TextView tvUserId;
    private EasyVideoPlayer videoPlayer;
    private AnchorViewModel anchorViewModel;
    private ConsumeViewModel consumeViewModel;
    private RelationViewModel relationViewModel;
    private MineModel mineModel;

    private int attention;
    private Anchor anchor;
    private User user;
    private int callFreeCount = -1;

    private SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarView(com.hyphenate.easecallkit.R.id.top_view)
                .init();
        //订阅通话结束消息
        LiveDataBus.get().with(Constant.END_CALL_EVENT, CallEndEvent.class).observe(this, this::callEnd);
        //sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void initView() {
        super.initView();
        initViews();
        initData();
        initListener();

        setGiftVisibility();
    }

    private void initViews() {
        ivFaceunity = findViewById(com.hyphenate.easecallkit.R.id.iv_faceunity);
        ibGift = findViewById(com.hyphenate.easecallkit.R.id.btn_gift);
        imageVoiceCover = findViewById(com.hyphenate.easecallkit.R.id.iv_bg_cover);
        ivAttention = findViewById(com.hyphenate.easecallkit.R.id.opposite_iv_attention);
        tvUserId = findViewById(com.hyphenate.easecallkit.R.id.tv_id);
        videoPlayer = findViewById(com.hyphenate.easecallkit.R.id.evp_player);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        user = SpManager.getInstance().getCurrentUser();
        relationViewModel = new ViewModelProvider(this).get(RelationViewModel.class);
        anchorViewModel = new ViewModelProvider(this).get(AnchorViewModel.class);
        mineModel = new ViewModelProvider(this).get(MineModel.class);

        imageVoiceCover.setVisibility(View.VISIBLE);
        ImageLoader.get().load(imageVoiceCover, R.mipmap.icon_voice_chat_bg);

        tvUserId.setText("id:" + getUserId());


        relationViewModel.observerRelation().observe(this, s -> {
            if (anchor != null) {
                if (anchor.isFocus() == 1) {
                    anchor.setFocus(0);
                } else {
                    anchor.setFocus(1);
                }
            }
        });

        mineModel.selectBalance().observe(this, balance -> callFreeCount = balance.getFocNum());//

        getAnchorDetailById();

    }

    private void initListener() {
        ivAttention.setOnClickListener(this);
        ivFaceunity.setOnClickListener(this);
        ibGift.setOnClickListener(this);
        chronometer.setOnChronometerTickListener(this::chronometerTick);
    }

    /**
     * 正在通话
     */
    @Override
    protected void makeOngoingStatus() {
        LogUtils.e("makeOngoingStatus");
        super.makeOngoingStatus();
        setBeautyVisibility();
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            imageVoiceCover.setVisibility(View.GONE);
            if (videoPlayer != null) {
                videoPlayer.setVisibility(View.GONE);
                videoPlayer.stop();
            }
            ivFaceunity.setVisibility(View.GONE);
        } else {
            imageVoiceCover.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 来电
     */
    @Override
    protected void makeComingStatus() {
        super.makeComingStatus();
        LogUtils.e("makeComingStatus");
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            if (videoPlayer != null) {
                videoPlayer.setVisibility(View.VISIBLE);
                videoPlayer.start();
            }
        } else {
            if (videoPlayer != null) {
                videoPlayer.setVisibility(View.GONE);
                videoPlayer.stop();
            }
        }
    }

    /**
     * ivFaceunity 显示或隐藏
     */
    private void setBeautyVisibility() {
        if (callType == EaseCallType.SINGLE_VOICE_CALL) {
            ivFaceunity.setVisibility(View.GONE);
        } else if (user.getSex() == Sex.MAN.ordinal()) {
            ivFaceunity.setVisibility(View.GONE);
        } else {
            ivFaceunity.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 礼物显示或隐藏
     */
    private void setGiftVisibility() {
        if (user.getSex() == Sex.WOMAN.ordinal()) {
            ibGift.setVisibility(View.GONE);
        } else {
            ibGift.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 男方计时监听
     */
    private void chronometerTick(MyChronometer chronometer) {
        if (user == null)
            return;
        long callTime = chronometer.getCostSeconds();
        if (callTime >= 1 && user.getSex() == Sex.MAN.ordinal()) {
            //SVIP 免费3次，每次1分钟
            if (SendMsgHelper.getInstance().isSuperVip() && callFreeCount > 0) {
                if (callTime == 60) {
                    hangupCall();
                }
            } else {
                double time = Math.ceil((callTime + 1) * 1d / 60);
                int price = getCallPrice();
                if (time > Math.floor(SpManager.getDiamondBalance() * 1d / price)) {
                    hangupCall();
                }
                //预扣费
                if (callTime % 60 == 0) {
                    SpManager.setDiamondBalance(price);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == com.hyphenate.easecallkit.R.id.opposite_iv_attention) {
            attention();
        } else if (id == com.hyphenate.easecallkit.R.id.btn_answer_call) {
            answerCall();
        } else if (id == com.hyphenate.easecallkit.R.id.iv_faceunity) {
            showBeautySetting();
        } else if (id == com.hyphenate.easecallkit.R.id.btn_gift) {
            GiftFragment giftFragment = new GiftFragment(username);
            giftFragment.show(getSupportFragmentManager(), "gift");
            giftFragment.setGiftGiveCallback(this::giftGive);
        }
        super.onClick(view);
    }

    /**
     * 美颜设置
     */
    private void showBeautySetting() {
        TiPanelLayout tiPanelLayout = new TiPanelLayout(this);
        tiPanelLayout.init(TiSDKManager.getInstance());
        tiPanelLayout.setAlwaysVisibleTiBeautyIV(true);
        tiPanelLayout.showBeautyModeContainer();
        addContentView(tiPanelLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 赠送礼物
     *
     * @param gift
     */
    private void giftGive(Gift gift) {
        ToastUtils.showToast(R.string.gift_send_success);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(Constant.GIVE_GIFT_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GIVE_GIFT, GsonUtils.toJson(gift));
        body.setParams(params);
        message.setBody(body);
        message.setTo(username);
        EMClient.getInstance().chatManager().sendMessage(message);
        if (gift.isSpecialEffects() == 1 && gift.getSpecialPic().endsWith(".gif")) {
            GifPlayActivity.launcher(gift.getSpecialPic());
        }
    }


    /**
     * 通话结束
     *
     * @param callEndEvent
     */
    private void callEnd(CallEndEvent callEndEvent) {
        callRecord(callEndEvent);
        deductBalance(callEndEvent);
        sendCallEndMessage(callEndEvent);
    }

    /**
     * 主叫发送 通话结束消息
     */
    private void sendCallEndMessage(CallEndEvent callEndEvent) {
        if (!isInComingCall) {
            if (callEndEvent.getDuration() > 0) {
                stopRecording(callEndEvent);
            } else {
                sendMessage(callEndEvent, "");
            }
        }
    }

    /**
     * 发送消息
     *
     * @param callEndEvent
     * @param recordUrl
     */
    private void sendMessage(CallEndEvent callEndEvent, String recordUrl) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(Constant.CALL_END_MESSAGE_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.REASON, callEndEvent.getEndReason() + "");
        params.put(Constant.CALL_TIME, callEndEvent.getDuration() / 1000 + "");
        params.put(Constant.CALL_TYPE, callEndEvent.getCallType() + "");
        params.put(Constant.RECORD_URL, recordUrl);
        body.setParams(params);
        message.setBody(body);
        message.setTo(username);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 主叫上传呼叫记录
     *
     * @param endEvent
     */
    private void callRecord(CallEndEvent endEvent) {
        if (!isInComingCall) {
            String userId = username;
            if (username.contains(Constant.HX_ID)) {
                userId = username.replace(Constant.HX_ID, "");
            }
            CallRecordBody body = new CallRecordBody();

            int callType, callState;
            if (endEvent.getCallType() == EaseCallType.SINGLE_VIDEO_CALL.code) {
                callType = 4;
            } else {
                callType = 3;
            }
            callState = 1;

            body.setTag(callType);
            body.setCallState(callState);
            body.setTime((int) (endEvent.getDuration() / 1000));
            if (remoteUId == 0 || remoteUId == -1) {
                body.setResponse(0);
            } else {
                body.setResponse(1);
            }
            body.setUserId(Integer.parseInt(userId));

            UserRepository.get().callRecord(body).enqueue(new Callback<AppResponse<String>>() {
                @Override
                public void onResponse(@NonNull Call<AppResponse<String>> call, @NonNull Response<AppResponse<String>> response) {
                }

                @Override
                public void onFailure(@NonNull Call<AppResponse<String>> call, Throwable t) {
                }
            });
        }
    }


    /**
     * 接语音或视频
     */
    protected void answerCall() {
        if (anchor == null || user == null)
            return;
        boolean isExpire = SendMsgHelper.getInstance().isExpireCall(anchor, callType);
        if (isExpire) {
            if (videoPlayer != null) {
                videoPlayer.setVisibility(View.GONE);
                videoPlayer.stop();
            }
            super.answerCall();
        }
    }

    /**
     * 主叫 开始音视频录制
     */
    @Override
    protected void startRecording() {
        super.startRecording();
        if (!isInComingCall) {
            int fromId, toId;
            fromId = user.getId();
            toId = Integer.parseInt(getUserId());
            UserRepository.get().startRecording(channelName, "", fromId, toId).enqueue(new Callback<AppResponse<String>>() {
                @Override
                public void onResponse(@NonNull Call<AppResponse<String>> call, @NonNull Response<AppResponse<String>> response) {
                    /*if (response.body() != null) {
                        AppResponse<String> appResp = response.body();
                        if (appResp.getData() != null) {
                            LogUtils.e(appResp.getData());
                        }
                    }*/
                }

                @Override
                public void onFailure(@NonNull Call<AppResponse<String>> call, @NonNull Throwable t) {
                }
            });
        }
    }

    /**
     * 主叫 停止音视频录制
     */
    protected void stopRecording(CallEndEvent callEndEvent) {
        if (!isInComingCall) {
            int fromId, toId;
            fromId = user.getId();
            toId = Integer.parseInt(getUserId());
            UserRepository.get().stopRecording(channelName, "", fromId, toId).enqueue(new Callback<AppResponse<String>>() {
                @Override
                public void onResponse(@NonNull Call<AppResponse<String>> call, @NonNull Response<AppResponse<String>> response) {
                    if (response.body() != null) {
                        AppResponse<String> appResp = response.body();
                        if (appResp.getCode() == 200) {
                            sendMessage(callEndEvent, appResp.getData());
                        } else {
                            sendMessage(callEndEvent, "");
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppResponse<String>> call, @NonNull Throwable t) {
                    sendMessage(callEndEvent, "");
                }
            });
        }
    }

    /**
     * 通话价格
     *
     * @return
     */
    private int getCallPrice() {
        int chatPrice = 0;
        if (anchor != null) {
            if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
                chatPrice = anchor.getVideoMinute();
            } else {
                chatPrice = anchor.getVoiceMinute();
            }
        }
        return chatPrice;
    }

    private void getAnchorDetailById() {
        if (username == null)
            return;
        try {
            anchorViewModel.setShowDialog(false);
            int userId = Integer.parseInt(getUserId());
            anchorViewModel.findAnchorDetailById(userId).observe(this, this::setAnchorInfo);
        } catch (NumberFormatException e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * anchor 信息
     *
     * @param anchor
     */
    private void setAnchorInfo(Anchor anchor) {
        this.anchor = anchor;
        if (anchor.isFocus() == 1) {
            ivAttention.setImageResource(R.mipmap.icon_followed_bg);
        } else {
            ivAttention.setImageResource(R.mipmap.icon_follow_bg);
        }
        initPlayer();
    }

    /**
     * 关注
     */
    private void attention() {
        User user = SpManager.getInstance().getCurrentUser();
        RelationBody body = new RelationBody(RelationType.ATTENTION.getRelationType(),
                user.getId(), Integer.parseInt(getUserId()));
        if (anchor != null) {
            if (this.anchor.isFocus() == 1) {
                relationViewModel.delAttention(body);
            } else {
                relationViewModel.addUserRelation(body);
            }
        }
    }

    /**
     * 初始化视频播放
     */
    private void initPlayer() {
        String proxyUrl = anchor.getCoverVideoUrl();
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            if (!TextUtils.isEmpty(proxyUrl)) {
                videoPlayer.setVisibility(View.VISIBLE);
                videoPlayer.setCallback(this);
                videoPlayer.disableControls();
                videoPlayer.setAutoFullscreen(true);
                videoPlayer.setLoop(true);
                videoPlayer.setAutoPlay(true);
                HttpProxyCacheServer proxy = ImLiveApp.getProxy();
                proxyUrl = proxy.getProxyUrl(proxyUrl);
                if (!TextUtils.isEmpty(proxyUrl)) {
                    Uri uri = Uri.parse(proxyUrl);
                    videoPlayer.setSource(uri);
                }
                //停止播放铃声
                super.stopPlayRing();
            } else {
                videoPlayer.setVisibility(View.GONE);
            }
        } else {
            videoPlayer.setVisibility(View.GONE);
        }

    }

    /**
     * 通话结束 客户方上传时长扣费
     */
    private void deductBalance(CallEndEvent endEvent) {
        LogUtils.e("deductBalance");
        User user = SpManager.getInstance().getCurrentUser();
        if (endEvent.getDuration() > 0 && user.getSex() == Sex.MAN.ordinal()) {
            String userId = getUserId();
            try {
                int callType, callState, isFree;
                if (endEvent.getCallType() == EaseCallType.SINGLE_VIDEO_CALL.code) {
                    callType = 4;
                } else {
                    callType = 3;
                }
                if (isInComingCall) {
                    callState = 2;
                } else {
                    callState = 1;
                }
                if (callFreeCount <= 0) {
                    isFree = 0;
                } else {
                    isFree = 1;
                }
                ChatRecordBody body = new ChatRecordBody();
                body.setTag(callType);
                body.setTime((int) (endEvent.getDuration() / 1000));
                body.setUserId(Integer.parseInt(userId));
                body.setCallState(callState);
                body.setFoc(isFree);
                UserRepository.get().deductBalance(body).enqueue(new Callback<AppResponse<Consume>>() {
                    @Override
                    public void onResponse(@NonNull Call<AppResponse<Consume>> call, @NonNull Response<AppResponse<Consume>> response) {
                        if (response.body() != null) {
                            AppResponse<Consume> appResp = response.body();
                            if (appResp.getData() != null) {
                                user.setUserDiamond(appResp.getData().getUserDiamond());
                                SpManager.getInstance().setCurrentUser(user);
                                LiveDataBus.get().with(Constant.DIAMOND_CHANGE).postValue(new DiamondChangeEvent());

                                try {
                                    String nickName = "";
                                    if (anchor != null) {
                                        nickName = anchor.getNickname();
                                    }
                                    AnchorEvaluateEvent event = new AnchorEvaluateEvent(appResp.getData().getRecordId(),
                                            Integer.parseInt(getUserId()), nickName);
                                    LiveDataBus.get().with(Constant.ANCHOR_EVALUATE).postValue(event);
                                } catch (NumberFormatException e) {
                                    LogUtils.e(e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AppResponse<Consume>> call, Throwable t) {
                        LogUtils.e("onFailure");
                    }
                });

            } catch (NumberFormatException e) {
                LogUtils.e(e.getMessage());
            }
        }
    }

    private String getUserId() {
        if (username.contains(Constant.HX_ID)) {
            return username.replace(Constant.HX_ID, "");
        }
        return username;
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

    public void startPlayer() {
        if (anchor != null && videoPlayer != null && !videoPlayer.isPlaying()) {
            videoPlayer.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlayer();
        if (sensorManager != null)
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                    SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_PROXIMITY:
                if (values[0] == 0.0) {// 贴近手机
                    LogUtils.d("hands up in calling activity");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {// 远离手机
                    LogUtils.d("hands moved in calling activity");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}