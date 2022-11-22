package com.hyphenate.easecallkit.ui;

import static com.hyphenate.easecallkit.utils.EaseMsgUtils.CALL_INVITE_EXT;
import static io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.R;
import com.hyphenate.easecallkit.base.EaseCallEndReason;
import com.hyphenate.easecallkit.base.EaseCallFloatWindow;
import com.hyphenate.easecallkit.base.EaseCallKitConfig;
import com.hyphenate.easecallkit.base.EaseCallKitListener;
import com.hyphenate.easecallkit.base.EaseCallKitTokenCallback;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.base.EaseGetUserAccountCallback;
import com.hyphenate.easecallkit.base.EaseUserAccount;
import com.hyphenate.easecallkit.event.AlertEvent;
import com.hyphenate.easecallkit.event.AnswerEvent;
import com.hyphenate.easecallkit.event.BaseEvent;
import com.hyphenate.easecallkit.event.CallCancelEvent;
import com.hyphenate.easecallkit.event.ConfirmCallEvent;
import com.hyphenate.easecallkit.event.ConfirmRingEvent;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallAction;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easecallkit.utils.EaseCallState;
import com.hyphenate.easecallkit.utils.EaseMsgUtils;
import com.hyphenate.easecallkit.widget.EaseImageView;
import com.hyphenate.easecallkit.widget.MyChronometer;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cn.tillusory.sdk.TiSDKManager;
import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.Constant;
import io.agora.capture.video.camera.VideoCapture;
import io.agora.framework.RtcVideoConsumer;
import io.agora.framework.TiPreprocessor;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/11/2021
 */
public class EaseVideoCallActivity extends EaseBaseCallActivity implements View.OnClickListener {

    protected static final String TAG = EaseVideoCallActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private View rootView;
    private Group comingBtnContainer, gpVideoComingCall;
    private ImageButton refuseBtn;
    private ImageButton answerBtn;
//    private ImageButton hangupBtn;

    //    private Group groupHangUp;
    protected Group groupUseInfo, groupBottomContainer;
    private Group groupOngoingSettings;
    private TextView nickTextView;
    private boolean isMuteState = false;
    private boolean isHandsfreeState;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private ImageButton switchCameraBtn;
    protected MyChronometer chronometer;
    private boolean surfaceStateChange = false;
    private EaseImageView avatarView;
    private TextView call_stateView;
    private TextView tvHangUpCountdown;

    private Group videoCallingGroup;
    private Group voiceCallingGroup;
    private TextView tv_nick_voice;

    private Group videoCalledGroup;
    private Group voiceCalledGroup;

    //    private RelativeLayout video_transe_layout;
//    private RelativeLayout video_transe_comming_layout;
//    private ImageButton btn_voice_trans;
    private TextView tv_call_state_voice, tv_coming_nick, tvNetworkStatus;
    private EaseImageView iv_avatar_voice;
    private ImageButton float_btn;

    //判断是发起者还是被邀请
    protected boolean isInComingCall;
    // Judge whether is ongoing call
    protected boolean isOngoingCall;
    protected String username;
    protected String channelName;

    protected AudioManager audioManager;
    protected Ringtone ringtone;

    private boolean mMuted = false;
    private boolean mCallEnd = false;
    volatile private boolean mConfirm_ring = false;
    private String tokenUrl;
    protected int remoteUId = 0;
    private boolean changeFlag = true;
    boolean transVoice = false;
    private String headUrl = null;
    private Bitmap headBitMap;
    private String ringFile;
    private MediaPlayer mediaPlayer;


    // 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
    protected RelativeLayout localSurface_layout;
    protected RelativeLayout oppositeSurface_layout;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    protected EaseCallType callType;
    //    private View Voice_View;
    private TimeHandler timehandler;

    private RtcEngine mRtcEngine;
    private boolean isMuteVideo = false;
    private String agoraAppId = null;
    // Camera direction: front or back
    private boolean isCameraFront;

    //用于防止多次打开请求悬浮框页面
    private boolean requestOverlayPermission;
    protected boolean videoDecoded = false;// 视频是否已经解码

    //加入频道Uid Map
    private Map<Integer, EaseUserAccount> uIdMap = new HashMap<>();
    EaseCallKitListener callKitListener = EaseCallKit.getInstance().getCallListener();

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onError(int err) {
            super.onError(err);
            Log.e(TAG, "IRtcEngineEventHandler onError:" + err);
            if (callKitListener != null) {
                callKitListener.onCallError(EaseCallKit.EaseCallError.RTC_ERROR, err, "rtc error");
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.e(TAG, "onJoinChannelSuccess channel:" + channel + " uid:" + uid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openSpeakerOn();
                    if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VOICE_CALL) {
                        handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                    }
                    if (!isInComingCall) {
                        //发送邀请信息
                        if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                            handler.sendEmptyMessage(EaseMsgUtils.MSG_MAKE_SIGNAL_VIDEO);
                        } else {
                            handler.sendEmptyMessage(EaseMsgUtils.MSG_MAKE_SIGNAL_VOICE);
                        }
                        //开始定时器
                        timehandler.startTime();
                    }
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.e(TAG, "onUserJoined");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //检测到对方进来
                    makeOngoingStatus();
                    remoteUId = uid;
                    startCount();//开始计时
                    String userName = null;
                    if (uIdMap != null) {
                        EaseUserAccount account = uIdMap.get(uid);
                        if (account != null) {
                            userName = uIdMap.get(uid).getUserName();
                        }
                    }
                    setUserJoinChannelInfo(null, uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.e(TAG, "onUserOffline");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //检测到对方退出 自己退出
                    exitChannel();
                    if (uIdMap != null) {
                        uIdMap.remove(uid);
                    }
                    if (callKitListener != null) {
                        //对方挂断
                        long time = getChronometerSeconds(chronometer);
                        Log.e("通话时长：", time + "");
                        callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonHangup, time * 1000);
                    }
                }
            });
        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            Log.e(TAG, "audioState:" + state + ",reason:" + reason);
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(state == Constants.REMOTE_AUDIO_STATE_STARTING){
                        if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VOICE_CALL) {
                            voiceCalledGroup.setVisibility(View.VISIBLE);
                            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                        }
                    }
                }
            });*/
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            Log.e(TAG, "videoState:" + state + ",reason:" + reason);
            runOnUiThread(() -> {
                if (state == Constants.REMOTE_VIDEO_STATE_DECODING) {
                    remoteUId = uid;
                    if (callType == EaseCallType.SINGLE_VIDEO_CALL && !videoDecoded) {
                        setupRemoteVideo(uid);
                    }
                    tvNetworkStatus.setVisibility(View.GONE);
                } else if (state == Constants.REMOTE_VIDEO_STATE_FROZEN) {
                    tvNetworkStatus.setVisibility(View.VISIBLE);
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_video_call);

        //初始化
        if (savedInstanceState == null) {
            initParams(getIntent().getExtras());
        } else {
            initParams(savedInstanceState);
        }

        //Init View
        initView();
        checkFloatIntent(getIntent());
        //增加LiveData监听
        addLiveDataObserver();

        //开启设备权限
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
        }

        timehandler = new TimeHandler();
        //被叫倒计时
        if (isInComingCall)
            timehandler.startCountdown();

        EaseCallKit.getInstance().getNotifier().reset();

        //加载头像图片
        loadHeadImage();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVideoManager != null)
            mVideoManager.startCapture();
    }

    private void initParams(Bundle bundle) {
        if (bundle != null) {
            isInComingCall = bundle.getBoolean("isComingCall", false);
            username = bundle.getString("username");
            channelName = bundle.getString("channelName");
            int uId = bundle.getInt("uId", -1);
            callType = EaseCallKit.getInstance().getCallType();
            if (uId == -1) {
                EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
            } else {
                isOngoingCall = true;
            }
        } else {
            isInComingCall = EaseCallKit.getInstance().getIsComingCall();
            username = EaseCallKit.getInstance().getFromUserId();
            channelName = EaseCallKit.getInstance().getChannelName();
            EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
        }
    }

    public void initView() {
        tvHangUpCountdown = findViewById(R.id.tv_tips);

        refuseBtn = findViewById(R.id.btn_refuse_call);
        answerBtn = findViewById(R.id.btn_answer_call);
        //hangupBtn = findViewById(R.id.btn_hangup_call);
        comingBtnContainer = findViewById(R.id.ll_coming_call);
        gpVideoComingCall = findViewById(R.id.gp_video_coming_call);
        avatarView = findViewById(R.id.iv_avatar);
        iv_avatar_voice = findViewById(R.id.iv_avatar_voice);
        tv_coming_nick = findViewById(R.id.tv_coming_nick);
        tvNetworkStatus = findViewById(R.id.tv_network_status);

        muteImage = findViewById(R.id.iv_mute);
        handsFreeImage = findViewById(R.id.iv_handsfree);
        switchCameraBtn = findViewById(R.id.btn_switch_camera);

        //呼叫中页面
        videoCallingGroup = findViewById(R.id.ll_video_calling);
        voiceCallingGroup = findViewById(R.id.ll_voice_calling);

        tv_nick_voice = findViewById(R.id.tv_nick_voice);
        tv_call_state_voice = findViewById(R.id.tv_call_state_voice);

        headUrl = EaseCallKitUtils.getUserHeadImage(username);
        ringFile = EaseCallKitUtils.getRingFile();

        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            videoCallingGroup.setVisibility(View.VISIBLE);
            voiceCallingGroup.setVisibility(View.GONE);
        } else {
            videoCallingGroup.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.VISIBLE);
            //hangupBtn.setVisibility(View.GONE);
            tv_nick_voice.setText(EaseCallKitUtils.getUserNickName(username));
        }


        //通话中页面
        videoCalledGroup = findViewById(R.id.ll_video_called);
        voiceCalledGroup = findViewById(R.id.ll_voice_control);
        voiceCalledGroup.setVisibility(View.GONE);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        //hangupBtn.setOnClickListener(this);

        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);

        // local surfaceview
        localSurface_layout = findViewById(R.id.local_surface_layout);
        // remote surfaceview
        oppositeSurface_layout = findViewById(R.id.opposite_surface_layout);
        //groupHangUp = findViewById(R.id.group_hang_up);
        groupUseInfo = findViewById(R.id.group_use_info);
        groupBottomContainer = findViewById(R.id.group_bottom_container);
        groupOngoingSettings = findViewById(R.id.group_ongoing_settings);
        nickTextView = findViewById(R.id.tv_nick);
        chronometer = findViewById(R.id.chronometer);
        call_stateView = findViewById(R.id.tv_call_state);

        localSurface_layout.setOnClickListener(this);

        rootView = ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);

        float_btn = findViewById(R.id.btn_call_float);
        float_btn.setOnClickListener(this);

        if (isInComingCall) {
            call_stateView.setText(getApplicationContext().getString(R.string.invite_you_for_audio_and_video_call));
            tv_call_state_voice.setText(getApplicationContext().getString(R.string.invite_you_for_audio_and_video_call));
        } else {
            call_stateView.setText(getApplicationContext().getString(R.string.waiting_for_accept));
            tv_call_state_voice.setText(getApplicationContext().getString(R.string.waiting_for_accept));
        }

        //如果是语音通话
        if (callType == EaseCallType.SINGLE_VOICE_CALL) {
            rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));
            //sufaceview不可见
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            //groupBottomContainer.setVisibility(View.GONE);

            //语音通话UI可见
            avatarView.setVisibility(View.VISIBLE);
        } else {
            avatarView.setVisibility(View.GONE);
            //groupBottomContainer.setVisibility(View.VISIBLE);
        }
        tv_coming_nick.setText(EaseCallKitUtils.getUserNickName(username));
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (!isInComingCall) {
            //拨打电话状态
            makeCallStatus();

            //主叫加入频道
            initEngineAndJoinChannel();
        } else {
            //被呼叫状态
            makeComingStatus();

            //开始振铃
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            if (ringUri != null) {
                ringtone = RingtoneManager.getRingtone(this, ringUri);
            }
            AudioManager am = (AudioManager) this.getApplication().getSystemService(Context.AUDIO_SERVICE);
            int ringerMode = am.getRingerMode();
            if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                Log.e(TAG, "playRing start");
                playRing();
            }
        }

        if (isOngoingCall) {
            makeOngoingStatus();
        }

    }

    /**
     * 来电话的状态
     */
    protected void makeComingStatus() {
        videoCallingGroup.setVisibility(View.GONE);
        comingBtnContainer.setVisibility(View.VISIBLE);
        groupUseInfo.setVisibility(View.GONE);
        localSurface_layout.setVisibility(View.GONE);
        oppositeSurface_layout.setVisibility(View.GONE);
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            gpVideoComingCall.setVisibility(View.VISIBLE);
            groupOngoingSettings.setVisibility(View.GONE);
        } else {
            gpVideoComingCall.setVisibility(View.GONE);
            avatarView.setVisibility(View.VISIBLE);
            nickTextView.setVisibility(View.VISIBLE);
        }
        groupRequestLayout();
    }

    /**
     * 通话中的状态
     */
    protected void makeOngoingStatus() {
        isOngoingCall = true;
        comingBtnContainer.setVisibility(View.GONE);
        groupUseInfo.setVisibility(View.GONE);
        //groupHangUp.setVisibility(View.VISIBLE);
        callType = EaseCallKit.getInstance().getCallType();
        EaseCallFloatWindow.getInstance().setCallType(callType);
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            gpVideoComingCall.setVisibility(View.GONE);
            groupOngoingSettings.setVisibility(View.VISIBLE);

            localSurface_layout.setVisibility(View.VISIBLE);
            oppositeSurface_layout.setVisibility(View.VISIBLE);

            videoCalledGroup.setVisibility(View.VISIBLE);
            voiceCalledGroup.setVisibility(View.VISIBLE);
            //hangupBtn.setVisibility(View.VISIBLE);
            videoCallingGroup.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.GONE);
        } else {
            groupOngoingSettings.setVisibility(View.VISIBLE);
            avatarView.setVisibility(View.VISIBLE);
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            nickTextView.setVisibility(View.VISIBLE);
            videoCalledGroup.setVisibility(View.GONE);
            voiceCalledGroup.setVisibility(View.VISIBLE);
            //hangupBtn.setVisibility(View.VISIBLE);

            videoCallingGroup.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.VISIBLE);
            tv_nick_voice.setText(EaseCallKitUtils.getUserNickName(username));
            tv_call_state_voice.setText(getApplicationContext().getString(R.string.in_the_call));
        }

        groupRequestLayout();
    }

    /**
     * 拨打电话的状态
     */
    protected void makeCallStatus() {
        if (!isInComingCall && callType == EaseCallType.SINGLE_VOICE_CALL) {
            voiceCalledGroup.setVisibility(View.GONE);
        } else {
            voiceCalledGroup.setVisibility(View.GONE);
            //oppositeSurface_layout.setVisibility(View.GONE);
        }
        comingBtnContainer.setVisibility(View.GONE);
        groupUseInfo.setVisibility(View.VISIBLE);
        groupOngoingSettings.setVisibility(View.GONE);
        localSurface_layout.setVisibility(View.GONE);
        //groupHangUp.setVisibility(View.VISIBLE);
        groupRequestLayout();
    }

    protected void groupRequestLayout() {
        comingBtnContainer.requestLayout();
        voiceCalledGroup.requestLayout();
        //groupHangUp.requestLayout();
        groupUseInfo.requestLayout();
        groupOngoingSettings.requestLayout();
    }

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            EaseCallKitConfig config = EaseCallKit.getInstance().getCallKitConfig();
            if (config != null) {
                agoraAppId = config.getAgoraAppId();
            }
            mRtcEngine = RtcEngine.create(getBaseContext(), agoraAppId, mRtcEventHandler);
            //因为有小程序 设置为直播模式 角色设置为主播
            mRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(CLIENT_ROLE_BROADCASTER);

            EaseCallFloatWindow.getInstance().setRtcEngine(getApplicationContext(), mRtcEngine);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private CameraVideoManager mVideoManager;
    private static final int CAPTURE_WIDTH = 1280;
    private static final int CAPTURE_HEIGHT = 720;
    private static final int CAPTURE_FRAME_RATE = 24;

    private void initVideoModule() {
        mVideoManager = new CameraVideoManager(getApplicationContext(),
                new TiPreprocessor(getApplicationContext()));

        mVideoManager.setCameraStateListener(new VideoCapture.VideoCaptureStateListener() {
            @Override
            public void onFirstCapturedFrame(int width, int height) {
                Log.i(TAG, "onFirstCapturedFrame: " + width + "x" + height);
            }

            @Override
            public void onCameraCaptureError(int error, String msg) {
                Log.i(TAG, "onCameraCaptureError: error:" + error + " " + msg);
                if (mVideoManager != null) {
                    // When there is a camera error, the capture should
                    // be stopped to reset the internal states.
                    mVideoManager.stopCapture();
                }
            }
        });

        mVideoManager.setPictureSize(CAPTURE_WIDTH, CAPTURE_HEIGHT);
        mVideoManager.setFrameRate(CAPTURE_FRAME_RATE);
        mVideoManager.setFacing(Constant.CAMERA_FACING_FRONT);
        mVideoManager.setLocalPreviewMirror(Constant.MIRROR_MODE_AUTO);

        mRtcEngine.setVideoSource(new RtcVideoConsumer());
    }

    private void setupVideoConfig() {
        if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_1280x720,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
            isCameraFront = true;
            initVideoModule();
        } else {
            mRtcEngine.disableVideo();
        }
    }

    private void setupLocalVideo() {
        if (isFloatWindowShowing()) {
            return;
        }
        setLocalVideo(oppositeSurface_layout, false);
    }

    private void setLocalVideo(ViewGroup surfaceViewContainer, boolean isMediaOverlay) {
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            SurfaceView localView = new SurfaceView(this);
            if (isMediaOverlay)
                localView.setZOrderMediaOverlay(true);
            localView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            surfaceViewContainer.removeAllViews();
            surfaceViewContainer.addView(localView);
            mVideoManager.setLocalPreview(localView);
            /*SurfaceView localView = RtcEngine.CreateRendererView(getBaseContext());
            if (isMediaOverlay)
                localView.setZOrderMediaOverlay(true);
            surfaceViewContainer.addView(localView);
            mLocalVideo = new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
            mRtcEngine.setupLocalVideo(mLocalVideo);*/
        } else {
            SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
            surfaceViewContainer.addView(view);
            mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
            mRtcEngine.setupLocalVideo(mLocalVideo);
        }
    }

    private void setRemoteVideo(ViewGroup surfaceViewContainer, boolean isMediaOverlay, int uid) {
        SurfaceView remoteView = RtcEngine.CreateRendererView(getBaseContext());
        Log.e(TAG, "remoteView:" + remoteView);
        if (isMediaOverlay)
            remoteView.setZOrderMediaOverlay(true);
        surfaceViewContainer.removeAllViews();
        surfaceViewContainer.addView(remoteView);
        mRemoteVideo = new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void setupRemoteVideo(int uid) {
        videoDecoded = true;
        setRemoteVideo(oppositeSurface_layout, false, uid);
        setLocalVideo(localSurface_layout, true);
    }

    /**
     * 加入频道
     */
    private void joinChannel() {
        EaseCallKitConfig callKitConfig = EaseCallKit.getInstance().getCallKitConfig();
        if (callKitListener != null && callKitConfig != null && callKitConfig.isEnableRTCToken()) {
            callKitListener.onGenerateToken(EMClient.getInstance().getCurrentUser(), channelName, EMClient.getInstance().getOptions().getAppKey(), new EaseCallKitTokenCallback() {
                @Override
                public void onSetToken(String token, int uId) {
                    Log.e(TAG, "onSetToken token:" + token + " uid: " + uId);
                    //获取到Token uid加入频道
                    mRtcEngine.joinChannel(token, channelName, null, uId);
                    //自己信息加入uIdMap
                    uIdMap.put(uId, new EaseUserAccount(uId, EMClient.getInstance().getCurrentUser()));
                }

                @Override
                public void onGetTokenError(int error, String errorMsg) {
                    Log.e(TAG, "onGenerateToken error :" + error + " errorMsg:" + errorMsg);
                    //获取Token失败,退出呼叫
                    exitChannel();
                }
            });
        }
    }

    private void changeCameraDirection(boolean isFront) {
        if (isCameraFront != isFront) {
            if (mVideoManager != null) {
                mVideoManager.switchCamera();
            }

            if (mVideoManager == null && mRtcEngine != null) {
                mRtcEngine.switchCamera();
            }
            isCameraFront = isFront;
        }
    }

    //是否接听 false 没有， true 接听
    protected boolean isAnswer = false;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_refuse_call) {
            if (isInComingCall && !isAnswer) {
                stopCount();
                stopPlayRing();
                //发送拒绝消息
                AnswerEvent event = new AnswerEvent();
                event.result = EaseMsgUtils.CALL_ANSWER_REFUSE;
                event.callId = EaseCallKit.getInstance().getCallID();
                event.callerDevId = EaseCallKit.getInstance().getClallee_devId();
                event.calleeDevId = EaseCallKit.deviceId;
                sendCmdMsg(event, username);
            } else {
                hangupCall();
            }
        } /*else if (id == R.id.btn_answer_call) {

        }*/ else if (id == R.id.local_surface_layout) {
            changeSurface();
        } else if (id == R.id.btn_call_float) {
            showFloatWindow();
        } else if (id == R.id.iv_mute) { // mute
            if (isMuteState) {
                // resume voice transfer
                muteImage.setImageResource(R.drawable.call_mute_normal);
            } else {
                // pause voice transfer
                muteImage.setImageResource(R.drawable.call_mute_on);

            }
            /*if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
                mRtcEngine.muteLocalVideoStream(!isMuteState);
            } else {
            }*/
            mRtcEngine.muteLocalAudioStream(!isMuteState);
            isMuteState = !isMuteState;
        } else if (id == R.id.iv_handsfree) { // handsfree
            if (isHandsfreeState) {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_normal);
                closeSpeakerOn();
            } else {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                openSpeakerOn();
            }
        } else if (id == R.id.btn_switch_camera) {
            changeCameraDirection(!isCameraFront);
        }
    }

    /**
     * 接听
     */
    protected void answerCall() {
        isAnswer = true;
        if (isInComingCall) {
            timehandler.stopCountdown();
            tvHangUpCountdown.setVisibility(View.GONE);
            tvHangUpCountdown.setText("");
            stopPlayRing();
            //发送接听消息
            AnswerEvent event = new AnswerEvent();
            event.result = EaseMsgUtils.CALL_ANSWER_ACCEPT;
            event.callId = EaseCallKit.getInstance().getCallID();
            event.callerDevId = EaseCallKit.getInstance().getClallee_devId();
            event.calleeDevId = EaseCallKit.deviceId;
            if (TextUtils.isEmpty(username)) {
                username = EaseCallKit.getInstance().getFromUserId();
            }
            if (TextUtils.isEmpty(channelName)) {
                channelName = EaseCallKit.getInstance().getChannelName();
            }
            sendCmdMsg(event, username);
        }
    }

    /**
     * 挂断
     */
    protected void hangupCall() {
        stopCount();
        if (remoteUId == 0) {
            CallCancelEvent cancelEvent = new CallCancelEvent();
            sendCmdMsg(cancelEvent, username);
        } else {
            exitChannel();
            if (callKitListener != null) {
                //通话结束原因挂断
                long time = getChronometerSeconds(chronometer);
                Log.e("通话时长1：", time + "");
                callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonHangup, time * 1000);
            }
        }
    }

    private void changeSurface() {
        if (changeFlag) {
            setRemoteVideo(localSurface_layout, true, remoteUId);
            setLocalVideo(oppositeSurface_layout, false);
        } else {
            setRemoteVideo(oppositeSurface_layout, false, remoteUId);
            setLocalVideo(localSurface_layout, true);
        }
        changeFlag = !changeFlag;
    }

    /**
     * 离开频道
     */
    private void leaveChannel() {
        // 离开当前频道。
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    void changeVideoVoiceState() {
        Log.e(TAG, "changeVideoVoiceState");
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {//切换到视频通话UI
            //语音通话UI可见
//            Voice_View.setVisibility(View.GONE);
            avatarView.setVisibility(View.GONE);

            //sufaceview不可见
            localSurface_layout.setVisibility(View.VISIBLE);
            oppositeSurface_layout.setVisibility(View.VISIBLE);

            makeOngoingStatus();
        } else { // 切换到音频通话UI
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));

            //已经在通话中
            if (EaseCallKit.getInstance().getCallState() == EaseCallState.CALL_ANSWERED) {
                //语音通话UI可见
//                Voice_View.setVisibility(View.VISIBLE);
                avatarView.setVisibility(View.VISIBLE);
                tv_call_state_voice.setText(getApplicationContext().getString(R.string.in_the_call));
                makeOngoingStatus();
            } else {
                localSurface_layout.setVisibility(View.GONE);
                oppositeSurface_layout.setVisibility(View.GONE);
                rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));

                if (isInComingCall) {
                    tv_call_state_voice.setText(getApplicationContext().getString(R.string.invite_you_for_audio_and_video_call));
                } else {
                    tv_call_state_voice.setText(getApplicationContext().getString(R.string.waiting_for_accept));
//                    if(!isInComingCall){
//                        voiceCalledGroup.setVisibility(View.VISIBLE);
//                    }
                }
                videoCallingGroup.setVisibility(View.GONE);
//                video_transe_layout.setVisibility(View.GONE);
//                video_transe_comming_layout.setVisibility(View.GONE);
                voiceCallingGroup.setVisibility(View.VISIBLE);
                tv_nick_voice.setText(EaseCallKitUtils.getUserNickName(username));
            }
            loadHeadImage();
        }
    }

    /**
     * 增加LiveData监听
     */
    protected void addLiveDataObserver() {
        EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString(), BaseEvent.class).observe(this, event -> {
            if (event != null) {
                Log.e(TAG, "callAction:" + event.callAction.state);
                switch (event.callAction) {
                    case CALL_ALERT:
                        AlertEvent alertEvent = (AlertEvent) event;
                        //判断会话是否有效
                        ConfirmRingEvent ringEvent = new ConfirmRingEvent();
                        if (TextUtils.equals(alertEvent.callId, EaseCallKit.getInstance().getCallID())
                                && EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_ANSWERED) {
                            //发送会话有效消息
                            ringEvent.calleeDevId = alertEvent.calleeDevId;
                            ringEvent.callId = alertEvent.callId;
                            ringEvent.valid = true;
                            sendCmdMsg(ringEvent, username);
                        } else {
                            //发送会话无效消息
                            ringEvent.calleeDevId = alertEvent.calleeDevId;
                            ringEvent.callId = alertEvent.callId;
                            ringEvent.valid = false;
                            sendCmdMsg(ringEvent, username);
                        }
                        //已经发送过会话确认消息
                        mConfirm_ring = true;
                        break;
                    case CALL_CANCEL:
                        if (!isInComingCall) {
                            //停止仲裁定时器
                            timehandler.stopTime();
                        }
                        //取消通话
                        exitChannel();
                        if (callKitListener != null) {
                            //对方取消
                            callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonRemoteCancel, 0);
                        }
                        break;
                    case CALL_ANSWER:
                        AnswerEvent answerEvent = (AnswerEvent) event;
                        ConfirmCallEvent callEvent = new ConfirmCallEvent();
                        boolean transVoice = answerEvent.transVoice;
                        callEvent.calleeDevId = answerEvent.calleeDevId;
                        callEvent.callerDevId = answerEvent.callerDevId;
                        callEvent.result = answerEvent.result;
                        callEvent.callId = answerEvent.callId;
                        if (TextUtils.equals(answerEvent.result, EaseMsgUtils.CALL_ANSWER_BUSY)) {
                            if (!mConfirm_ring) {
                                //退出频道
                                timehandler.stopTime();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //提示对方正在忙碌中
                                        String info = getString(R.string.The_other_is_busy);
                                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                                        //退出通话
                                        exitChannel();

                                        if (callKitListener != null) {
                                            //对方正在忙碌中
                                            callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonBusy, 0);
                                        }
                                    }
                                });
                            } else {
                                timehandler.stopTime();
                                sendCmdMsg(callEvent, username);
                            }
                        } else if (TextUtils.equals(answerEvent.result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                            //设置为接听
                            EaseCallKit.getInstance().setCallState(EaseCallState.CALL_ANSWERED);
                            timehandler.stopTime();
                            sendCmdMsg(callEvent, username);
                            if (transVoice) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callType = EaseCallType.SINGLE_VOICE_CALL;
                                        EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
                                        EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
                                        changeVideoVoiceState();
                                    }

                                });
                            }
                        } else if (TextUtils.equals(answerEvent.result, EaseMsgUtils.CALL_ANSWER_REFUSE)) {
                            timehandler.stopTime();
                            sendCmdMsg(callEvent, username);
                        }
                        break;
                    case CALL_INVITE:
                        //收到转音频事件
//                        InviteEvent inviteEvent = (InviteEvent) event;
//                        if (inviteEvent.type == EaseCallType.SINGLE_VOICE_CALL) {
//                            callType = EaseCallType.SINGLE_VOICE_CALL;
//                            EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
//                            EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
//                            if (mRtcEngine != null) {
//                                mRtcEngine.disableVideo();
//                            }
//                            changeVideoVoiceState();
//                        }
                        break;
                    case CALL_CONFIRM_RING:
                        break;
                    case CALL_CONFIRM_CALLEE:
                        Log.e(TAG, "CALL_CONFIRM_CALLEE");
                        ConfirmCallEvent confirmEvent = (ConfirmCallEvent) event;
                        String deviceId = confirmEvent.calleeDevId;
                        String result = confirmEvent.result;
                        timehandler.stopTime();
                        //收到的仲裁为自己设备
                        if (TextUtils.equals(deviceId, EaseCallKit.deviceId)) {
                            //收到的仲裁为接听
                            if (TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                                EaseCallKit.getInstance().setCallState(EaseCallState.CALL_ANSWERED);
                                //加入频道
                                initEngineAndJoinChannel();
                                makeOngoingStatus();
                                if (mVideoManager != null)
                                    mVideoManager.startCapture();
                            } else if (TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_REFUSE)) {
                                //退出通话
                                exitChannel();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //提示已在其他设备处理
                                    String info = null;
                                    if (TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                                        //已经在其他设备接听
                                        info = getString(R.string.The_other_is_recived);

                                    } else if (TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_REFUSE)) {
                                        //已经在其他设备拒绝
                                        info = getString(R.string.The_other_is_refused);
                                    }
                                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                                    //退出通话
                                    exitChannel();

                                    if (callKitListener != null) {
                                        //已经在其他设备处理
                                        callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonHandleOnOtherDevice, 0);
                                    }
                                }
                            });
                        }

                        break;
                }
            }
        });

        EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO, EaseCallUserInfo.class).observe(this, userInfo -> {
            if (userInfo != null) {
                if (TextUtils.equals(userInfo.getUserId(), username)) {
                    //更新本地头像昵称
                    EaseCallKit.getInstance().getCallKitConfig().setUserInfo(username, userInfo);
                    updateUserInfo();
                }
            }
        });
    }

    /**
     * 处理异步消息
     */
    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");

    {
        callHandlerThread.start();
    }

    protected Handler handler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100: // 1V1语音通话
                    sendInviteeMsg(username, EaseCallType.SINGLE_VOICE_CALL);
                    break;
                case 101: // 1V1视频通话
                    sendInviteeMsg(username, EaseCallType.SINGLE_VIDEO_CALL);
                    break;
                case 301: //停止事件循环线程
                    //防止内存泄漏
                    handler.removeMessages(100);
                    handler.removeMessages(101);
                    handler.removeMessages(102);
                    callHandlerThread.quit();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 发送通话邀请信息
     *
     * @param username
     * @param callType
     */
    private void sendInviteeMsg(String username, EaseCallType callType) {
        //更新昵称 头像
        setUserJoinChannelInfo(username, 0);
        mConfirm_ring = false;
        final EMMessage message;
        message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setBody(new EMCmdMessageBody("rtcCall"));
        message.setTo(username);
        /*final EMMessage message;
        if (callType == EaseCallType.SINGLE_VIDEO_CALL) {
            message = EMMessage.createTxtSendMessage(getApplicationContext().getString(R.string.invite_you_for_video_call), username);
        } else {
            message = EMMessage.createTxtSendMessage(getApplicationContext().getString(R.string.invite_you_for_audio_call), username);
        }*/
        message.setAttribute(EaseMsgUtils.CALL_ACTION, EaseCallAction.CALL_INVITE.state);
        message.setAttribute(EaseMsgUtils.CALL_CHANNELNAME, channelName);
        message.setAttribute(EaseMsgUtils.CALL_TYPE, callType.code);
        message.setAttribute(EaseMsgUtils.CALL_DEVICE_ID, EaseCallKit.deviceId);

        JSONObject object = EaseCallKit.getInstance().getInviteExt();
        if (object != null) {
            message.setAttribute(CALL_INVITE_EXT, object);
        } else {
            try {
                JSONObject obj = new JSONObject();
                message.setAttribute(CALL_INVITE_EXT, obj);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        //增加推送字段
        JSONObject extObject = new JSONObject();
        try {
            EaseCallType type = EaseCallKit.getInstance().getCallType();
            String info;
            if (type == EaseCallType.SINGLE_VOICE_CALL) {
                info = getApplication().getString(R.string.alert_request_voice, EMClient.getInstance().getCurrentUser());
            } else {
                info = getApplication().getString(R.string.alert_request_video, EMClient.getInstance().getCurrentUser());
            }
            extObject.putOpt("em_push_title", info);
            extObject.putOpt("em_push_content", info);
            extObject.putOpt("isRtcCall", true);
            extObject.putOpt("callType", type.code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("em_apns_ext", extObject);

        if (EaseCallKit.getInstance().getCallID() == null) {
            EaseCallKit.getInstance().setCallID(EaseCallKitUtils.getRandomString(10));
        }
        message.setAttribute(EaseMsgUtils.CLL_ID, EaseCallKit.getInstance().getCallID());
        message.setAttribute(EaseMsgUtils.CLL_TIMESTRAMEP, System.currentTimeMillis());
        message.setAttribute(EaseMsgUtils.CALL_MSG_TYPE, EaseMsgUtils.CALL_MSG_INFO);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Invite call success");
                if (callKitListener != null) {
                    callKitListener.onInViteCallMessageSent();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "Invite call error " + code + ", " + error);
                if (callKitListener != null) {
                    callKitListener.onCallError(EaseCallKit.EaseCallError.IM_ERROR, code, error);
                    callKitListener.onInViteCallMessageSent();
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 发送CMD回复信息
     *
     * @param username
     */
    private void sendCmdMsg(BaseEvent event, String username) {
        EaseCallKit.getInstance().sendCmdMsg(event, username, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "sendCmdMsg:" + event.callAction);
                if (event.callAction == EaseCallAction.CALL_CANCEL) {
                    //退出频道
                    resetState();

                    boolean cancel = ((CallCancelEvent) event).cancel;
                    runOnUiThread(() -> {
                        EaseCallEndReason easeCallEndReason = cancel ? EaseCallEndReason.EaseCallEndReasonCancel
                                : EaseCallEndReason.EaseCallEndReasonRemoteNoResponse;
                        if (callKitListener != null) {
                            callKitListener.onEndCallWithReason(callType, channelName, easeCallEndReason, 0);
                        }
                    });
                } else if (event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE) {
                    //不为接通状态 退出频道
                    if (!TextUtils.equals(((ConfirmCallEvent) event).result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                        resetState();
                        String result = ((ConfirmCallEvent) event).result;

                        //对方拒绝通话
                        if (TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_REFUSE)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (callKitListener != null) {
                                        callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonRefuse, 0);
                                    }
                                }
                            });
                        }
                    }
                } else if (event.callAction == EaseCallAction.CALL_ANSWER) {
                    //回复以后启动定时器，等待仲裁超时
                    timehandler.startTime();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "Invite call error " + code + ", " + error);
                if (callKitListener != null) {
                    callKitListener.onCallError(EaseCallKit.EaseCallError.IM_ERROR, code, error);
                }
                if (event.callAction == EaseCallAction.CALL_CANCEL) {
                    //退出频道
                    resetState();
                } else if (event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE) {
                    //不为接通状态 退出频道
                    if (!TextUtils.equals(((ConfirmCallEvent) event).result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                        resetState();
                    }
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private class TimeHandler extends Handler {
        private final int MSG_TIMER = 0;
        private final int MSG_COUNTDOWN = 1;
        private DateFormat dateFormat = null;
        private int timePassed = 0;
        private int timeCountdown = 0;

        public TimeHandler() {
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        public void startTime() {
            timePassed = 0;
            sendEmptyMessageDelayed(MSG_TIMER, 1000);
        }

        public void stopTime() {
            removeMessages(MSG_TIMER);
        }

        public void startCountdown() {
            timeCountdown = 0;
            sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
        }

        public void stopCountdown() {
            removeMessages(MSG_COUNTDOWN);
        }

        private long getIntervalTime() {
            long intervalTime;
            EaseCallKitConfig callKitConfig = EaseCallKit.getInstance().getCallKitConfig();
            if (callKitConfig != null) {
                intervalTime = callKitConfig.getCallTimeOut();
            } else {
                intervalTime = EaseMsgUtils.CALL_INVITE_INTERVAL;
            }
            return intervalTime;
        }

        private void sendCancelMsg() {
            CallCancelEvent cancelEvent = new CallCancelEvent();
            cancelEvent.cancel = false;
            cancelEvent.remoteTimeout = true;
            //对方超时未接通,发送取消
            sendCmdMsg(cancelEvent, username);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMER) {
                timePassed++;
                //Log.e("TAG", "TimeHandler timePassed: " + timePassed);
                String time = dateFormat.format(timePassed * 1000);
                if (timePassed * 1000L == getIntervalTime()) {
                    //主叫呼叫超时
                    timehandler.stopTime();
                    if (!isInComingCall) {
                        sendCancelMsg();
                    } else {
                        //被叫等待仲裁消息超时
                        exitChannel();
                        if (callKitListener != null) {
                            //对方接通超时
                            callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonRemoteNoResponse, 0);
                        }
                    }
                }
                sendEmptyMessageDelayed(MSG_TIMER, 1000);
                return;
            } else if (msg.what == MSG_COUNTDOWN) {
                timeCountdown++;
                if (timeCountdown * 1000L == getIntervalTime()) {
                    timehandler.stopCountdown();
                    sendCancelMsg();
                } else {
                    updateCountdown(getIntervalTime(), timeCountdown);
                    sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
                }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 被叫倒计时
     */
    public void updateCountdown(long totalTime, long countdown) {
        long count = totalTime / 1000L - countdown;
        if (tvHangUpCountdown.getVisibility() == View.GONE)
            tvHangUpCountdown.setVisibility(View.VISIBLE);

        if (count > 0) {
            tvHangUpCountdown.setText(String.format(getString(R.string.hang_up_of_time), count));
        } else {
            tvHangUpCountdown.setVisibility(View.GONE);
            tvHangUpCountdown.setText("");
        }
    }

    public long getChronometerSeconds(MyChronometer cmt) {
        if (cmt == null) {
            Log.e(TAG, "MyChronometer is null, can not get the cost seconds!");
            return 0;
        }
        return cmt.getCostSeconds();
    }

    /**
     * 加载用户配置头像
     *
     * @return
     */
    private void loadHeadImage() {
        if (!TextUtils.isEmpty(username)) {
            EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
            if (userProvider != null) {
                EaseUser mineUser = userProvider.getUser(EMClient.getInstance().getCurrentUser());
                if (mineUser != null) {
                    int defaultAvatar;
                    if (mineUser.getGender() == 0) {
                        defaultAvatar = com.hyphenate.easeui.R.drawable.icon_man_default;
                    } else {
                        defaultAvatar = com.hyphenate.easeui.R.drawable.icon_woman_default;
                    }
                    if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                        Glide.with(this).load(defaultAvatar).into(avatarView);
                    } else {
                        Glide.with(this).load(defaultAvatar).into(iv_avatar_voice);
                    }
                }
                EaseUser user = userProvider.getUser(username);
                if (user != null) {
                    if (!TextUtils.isEmpty(user.getNickname())) {
                        tv_coming_nick.setText(user.getNickname());
                        nickTextView.setText(user.getNickname());
                        tv_nick_voice.setText(user.getNickname());
                    }
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                            Glide.with(this).load(user.getAvatar()).into(avatarView);
                        } else {
                            Glide.with(this).load(user.getAvatar()).into(iv_avatar_voice);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置用户信息回调
     *
     * @param userName
     * @param uId
     */
    private void setUserJoinChannelInfo(String userName, int uId) {
        if (callKitListener != null) {
            callKitListener.onRemoteUserJoinChannel(channelName, userName, uId, new EaseGetUserAccountCallback() {
                @Override
                public void onUserAccount(List<EaseUserAccount> userAccounts) {
                    if (userAccounts != null && userAccounts.size() > 0) {
                        for (EaseUserAccount account : userAccounts) {
                            uIdMap.put(account.getUid(), account);
                        }
                    }
                    updateUserInfo();
                }

                @Override
                public void onSetUserAccountError(int error, String errorMsg) {
                    Log.e(TAG, "onRemoteUserJoinChannel error:" + error + "  errorMsg:" + errorMsg);
                }
            });
        }
    }

    /**
     * 更新本地头像昵称
     */
    private void updateUserInfo() {
        Log.e(TAG, "updateUserInfo");
        //更新本地头像昵称
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //头像
                headUrl = EaseCallKitUtils.getUserHeadImage(username);
                //昵称
                tv_coming_nick.setText(EaseCallKitUtils.getUserNickName(username));
                nickTextView.setText(EaseCallKitUtils.getUserNickName(username));
                tv_nick_voice.setText(EaseCallKitUtils.getUserNickName(username));
                loadHeadImage();
            }
        });
    }

    private void playRing() {
        if (ringFile != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(ringFile);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Log.e(TAG, "playRing play file");
                }
            } catch (IOException e) {
                mediaPlayer = null;
            }
        } else {
            Log.e(TAG, "playRing start play");
            if (ringtone != null) {
                ringtone.play();
                Log.e(TAG, "playRing play ringtone");
            }
            Log.e(TAG, "playRing start play end");
        }
    }

    protected void stopPlayRing() {
        if (ringFile != null) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null;
            }
        } else {
            if (ringtone != null) {
                ringtone.stop();
            }
        }
    }

    /**
     * 显示悬浮窗
     */
    @Override
    public void doShowFloatWindow() {
        super.doShowFloatWindow();
        if (chronometer != null) {
            EaseCallFloatWindow.getInstance().setCostSeconds(chronometer.getCostSeconds());
        }
        EaseCallFloatWindow.getInstance().show();
        boolean surface = true;
        if (isInComingCall && EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_ANSWERED) {
            surface = false;
        }
        EaseCallFloatWindow.getInstance().update(!changeFlag, 0, remoteUId, surface);
        EaseCallFloatWindow.getInstance().setCameraDirection(isCameraFront, changeFlag);
        moveTaskToBack(false);
    }

    /**
     * 开启扬声器
     */
    protected void openSpeakerOn() {
        isHandsfreeState = true;
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭扬声器
     */
    protected void closeSpeakerOn() {
        isHandsfreeState = false;
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetState() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInComingCall) {
                    stopPlayRing();
                }
                isOngoingCall = false;

                finish();
            }
        });
    }

    /**
     * 退出频道
     */
    void exitChannel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EMLog.i(TAG, "exit channel channelName: " + channelName);
                if (isFloatWindowShowing()) {
                    EaseCallFloatWindow.getInstance(getApplicationContext()).dismiss();
                }

                //重置状态
                EaseCallKit.getInstance().setCallState(EaseCallState.CALL_IDLE);
                EaseCallKit.getInstance().setCallID(null);
                resetState();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkFloatIntent(intent);
    }

    private void checkFloatIntent(Intent intent) {
        // 防止activity在后台被start至前台导致window还存在
        if (isFloatWindowShowing()) {
            EaseCallFloatWindow.SingleCallInfo callInfo = EaseCallFloatWindow.getInstance().getSingleCallInfo();
            if (callInfo != null) {
                remoteUId = callInfo.remoteUid;
                changeFlag = callInfo.changeFlag;
                isCameraFront = callInfo.isCameraFront;
                if (EaseCallKit.getInstance().getCallState() == EaseCallState.CALL_ANSWERED) {
                    if (changeFlag && remoteUId != 0) {
                        setRemoteVideo(oppositeSurface_layout, false, remoteUId);
                        setLocalVideo(localSurface_layout, false);
                    } else {
                        setLocalVideo(oppositeSurface_layout, false);
                        setRemoteVideo(localSurface_layout, false, remoteUId);
                    }
                } else {
                    if (!isInComingCall) {
                        setLocalVideo(oppositeSurface_layout, false);
                    }
                }
                changeCameraDirection(isCameraFront);
            }
            long totalCostSeconds = EaseCallFloatWindow.getInstance().getTotalCostSeconds();
            chronometer.setBase(SystemClock.elapsedRealtime() - totalCostSeconds * 1000);
            chronometer.start();
        }
        EaseCallFloatWindow.getInstance().dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EMLog.i(TAG, "onActivityResult: " + requestCode + ", result code: " + resultCode);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestOverlayPermission = false;
            // Result of window permission request, resultCode = RESULT_CANCELED
            if (Settings.canDrawOverlays(this)) {
                doShowFloatWindow();
            } else {
                Toast.makeText(this, getString(R.string.alert_window_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCount() {
        if (chronometer != null) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
        startRecording();
    }

    private void stopCount() {
        Log.e(TAG, "stopCount");
        if (chronometer != null) {
            chronometer.stop();
        }
    }

    /**
     * 停止事件循环
     */
    protected void releaseHandler() {
        handler.sendEmptyMessage(EaseMsgUtils.MSG_RELEASE_HANDLER);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        releaseHandler();
        if (timehandler != null) {
            timehandler.stopTime();
            timehandler.stopCountdown();
        }
        if (headBitMap != null) {
            headBitMap.recycle();
        }
        if (uIdMap != null) {
            uIdMap.clear();
        }
        if (!isFloatWindowShowing()) {
            EaseCallKit.getInstance().releaseCall();
            if (mVideoManager != null)
                mVideoManager.stopCapture();
            TiSDKManager.getInstance().destroy();
            leaveChannel();
            RtcEngine.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else {
            // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        exitChannelDisplay();
    }

    /**
     * 是否退出当前通话提示框
     */
    public void exitChannelDisplay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EaseVideoCallActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(EaseVideoCallActivity.this, R.layout.activity_exit_channel, null);
        dialog.setView(dialogView);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        dialog.show();

        final Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        final Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.e(TAG, "exitChannelDisplay  exit channel:");
                stopCount();
                if (remoteUId == 0) {
                    CallCancelEvent cancelEvent = new CallCancelEvent();
                    sendCmdMsg(cancelEvent, username);
                } else {
                    exitChannel();
                    if (callKitListener != null) {
                        //通话结束原因挂断
                        long time = getChronometerSeconds(chronometer);
                        callKitListener.onEndCallWithReason(callType, channelName, EaseCallEndReason.EaseCallEndReasonHangup, time * 1000);
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.e(TAG, "exitChannelDisplay not exit channel");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_IDLE) {
            showFloatWindow();
        }
    }

    /**
     * 开始录制
     */
    protected void startRecording() {
    }

}