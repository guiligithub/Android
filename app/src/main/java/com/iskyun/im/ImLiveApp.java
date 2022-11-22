package com.iskyun.im;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;
import com.hyphenate.easecallkit.base.EaseCallFloatWindow;
import com.iskyun.im.common.ActivityState;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.bean.OnlineStatus;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.helper.ImHelper;
import com.iskyun.im.ui.SplashActivity;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.commonsdk.UMConfigure;

import java.util.ArrayList;
import java.util.List;

import cn.net.shoot.sharetracesdk.ShareTrace;
import cn.tillusory.sdk.TiSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImLiveApp extends Application {

    private static ImLiveApp instance;
    private final AppActivityLifecycleCallbacks mLifecycleCallbacks = new AppActivityLifecycleCallbacks();
    private HttpProxyCacheServer proxy;
    private IWXAPI api;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks();
        TiSDK.initSDK("4915ed8c92ce479c9bdff85eb9a5d6b2", getApplicationContext());

        initHx();
        initUmeng();
        initWxApi();

        ShareTrace.init(this);
    }


    public static ImLiveApp get() {
        return instance;
    }

    public Activity getTopActivity() {
        return getLifecycleCallbacks().current();
    }

    public List<Activity> getActivities() {
        return getLifecycleCallbacks().getActivityList();
    }

    public IWXAPI getWXApi() {
        return api;
    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    /**
     * 视频缓存代理服务
     */
    public static HttpProxyCacheServer getProxy() {
        ImLiveApp app = (ImLiveApp) get();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }


    public AppActivityLifecycleCallbacks getLifecycleCallbacks() {
        return mLifecycleCallbacks;
    }

    private void initWxApi() {
        api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, true);
        api.registerApp(Constant.WX_APP_ID);
    }


    private void initHx() {
        // 初始化PreferenceManager
        SpManager.init(this);
        // init hx sdk
//        if(ImHelper.getInstance().getAutoLogin()) {
//            EMLog.i("DemoApplication", "application initHx");
//        }
        ImHelper.getInstance().init(this);

    }

    private void initUmeng() {
        UMConfigure.preInit(this, "62a0676105844627b5a62957", "Umeng");
        if (SpManager.getInstance().getUmengInit()) {
            UMConfigure.init(this, "62a0676105844627b5a62957", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        }
    }

    public static class AppActivityLifecycleCallbacks implements ActivityLifecycleCallbacks, ActivityState {

        private final List<Activity> activityList = new ArrayList<>();
        private final List<Activity> resumeActivity = new ArrayList<>();

        /**
         * 更新用户状态
         *
         * @param onlineStatus
         */
        private void updateStatusUserOnline(OnlineStatus onlineStatus) {
            UserRepository.get().updateStatusUserOnline(onlineStatus.getStatus())
                    .enqueue(new Callback<AppResponse<User>>() {
                        @Override
                        public void onResponse(@NonNull Call<AppResponse<User>> call, @NonNull Response<AppResponse<User>> response) {
                        }

                        @Override
                        public void onFailure(Call<AppResponse<User>> call, Throwable t) {
                        }
                    });
        }

        private void userLoginOrLogout(OnlineStatus status) {
            UserRepository.get().userLoginOrLogout(status.getStatus()).enqueue(new Callback<AppResponse<String>>() {
                @Override
                public void onResponse(@NonNull Call<AppResponse<String>> call, @NonNull Response<AppResponse<String>> response) {
                }

                @Override
                public void onFailure(@NonNull Call<AppResponse<String>> call, Throwable t) {
                }
            });
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            LogUtils.i("onActivityCreated " + activity.getLocalClassName());
            activityList.add(0, activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtils.i("onActivityStarted " + activity.getLocalClassName());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtils.i("onActivityResumed activity's taskId = " + activity.getTaskId() + " name: " + activity.getLocalClassName());
            if (!resumeActivity.contains(activity)) {
                resumeActivity.add(activity);
                if (resumeActivity.size() == 1) {
                    LogUtils.i("在前台了");
                    // do something
                    updateStatusUserOnline(OnlineStatus.ONLINE);
                    userLoginOrLogout(OnlineStatus.ONLINE);
                }
                restartSingleInstanceActivity(activity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtils.i("onActivityPaused " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            LogUtils.i("onActivityStopped " + activity.getLocalClassName());
            resumeActivity.remove(activity);
            if (resumeActivity.isEmpty()) {
                LogUtils.i("在后台了");
                Activity a = getOtherTaskSingleInstanceActivity(activity.getTaskId());
                if (isTargetSingleInstance(a) && !EaseCallFloatWindow.getInstance().isShowing()) {
                    makeTaskToFront(a);
                }
                updateStatusUserOnline(OnlineStatus.OFFLINE);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            LogUtils.i("onActivitySaveInstanceState " + activity.getLocalClassName());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LogUtils.i("onActivityDestroyed " + activity.getLocalClassName());
            activityList.remove(activity);
            if (activityList.isEmpty()) {
                userLoginOrLogout(OnlineStatus.OFFLINE);
            }
        }

        @Override
        public Activity current() {
            return activityList.size() > 0 ? activityList.get(0) : null;
        }

        @Override
        public List<Activity> getActivityList() {
            return activityList;
        }

        @Override
        public int count() {
            return activityList.size();
        }

        @Override
        public boolean isFront() {
            return resumeActivity.size() > 0;
        }

        /**
         * 跳转到目标activity
         *
         * @param cls
         */
        public void skipToTarget(Class<?> cls) {
            if (activityList.size() > 0) {
                current().startActivity(new Intent(current(), cls));
                for (Activity activity : activityList) {
                    activity.finish();
                }
            }

        }

        /**
         * finish target activity
         *
         * @param cls
         */
        public void finishTarget(Class<?> cls) {
            if (!activityList.isEmpty()) {
                for (Activity activity : activityList) {
                    if (activity.getClass() == cls) {
                        activity.finish();
                    }
                }
            }
        }

        /**
         * 判断app是否在前台
         *
         * @return
         */
        public boolean isOnForeground() {
            return !resumeActivity.isEmpty();
        }


        /**
         * 用于按下home键，点击图标，检查启动模式是singleInstance，且在activity列表中首位的Activity
         * 下面的方法，专用于解决启动模式是singleInstance, 为开启悬浮框的情况
         *
         * @param activity
         */
        private void restartSingleInstanceActivity(Activity activity) {
            boolean isClickByFloat = activity.getIntent().getBooleanExtra("isClickByFloat", false);
            if (isClickByFloat) {
                return;
            }
            //刚启动，或者从桌面返回app
            if (resumeActivity.size() == 1 && resumeActivity.get(0) instanceof SplashActivity) {
                return;
            }
            //至少需要activityList中至少两个activity
            if (resumeActivity.size() >= 1 && activityList.size() > 1) {
                Activity a = getOtherTaskSingleInstanceActivity(resumeActivity.get(0).getTaskId());
                if (a != null && !a.isFinishing() //没有正在finish
                        && a != activity //当前activity和列表中首个activity不相同
                        && a.getTaskId() != activity.getTaskId()
                        && !EaseCallFloatWindow.getInstance().isShowing()) {
                    LogUtils.i("启动了activity = " + a.getClass().getName());
                    activity.startActivity(new Intent(activity, a.getClass()));
                }
            }
        }

        private Activity getOtherTaskSingleInstanceActivity(int taskId) {
            if (taskId != 0 && activityList.size() > 1) {
                for (Activity activity : activityList) {
                    if (activity.getTaskId() != taskId) {
                        if (isTargetSingleInstance(activity)) {
                            return activity;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * 此方法用于设置启动模式为singleInstance的activity调用
         * 用于解决点击悬浮框后，然后finish当前的activity，app回到桌面的问题
         * 需要如下两个权限：
         * <uses-permission android:name="android.permission.GET_TASKS" />
         * <uses-permission android:name="android.permission.REORDER_TASKS"/>
         *
         * @param activity
         */
        public void makeMainTaskToFront(Activity activity) {
            //当前activity正在finish，且可见的activity列表中只有这个正在finish的activity,且没有销毁的activity个数大于等于2
            if (activity.isFinishing() && resumeActivity.size() == 1 && resumeActivity.get(0) == activity && activityList.size() > 1) {
                ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(20);
                for (int i = 0; i < runningTasks.size(); i++) {
                    ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(i);
                    ComponentName topActivity = taskInfo.topActivity;
                    //判断是否是相同的包名
                    if (topActivity != null && topActivity.getPackageName().equals(activity.getPackageName())) {
                        int taskId;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            taskId = taskInfo.taskId;
                        } else {
                            taskId = taskInfo.id;
                        }
                        //将任务栈置于前台
                        LogUtils.i("执行moveTaskToFront，current activity:" + activity.getClass().getName());
                        manager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
                    }
                }
            }
        }

        private boolean isTargetSingleInstance(Activity activity) {
            if (activity == null) {
                return false;
            }
            CharSequence title = activity.getTitle();
            if (TextUtils.equals(title, activity.getString(R.string.activity_label_video_call))
                    || TextUtils.equals(title, activity.getString(R.string.activity_label_multi_call))) {
                return true;
            }
            return false;
        }

        private void makeTaskToFront(Activity activity) {
            LogUtils.i("makeTaskToFront activity: " + activity.getLocalClassName());
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            manager.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
        }
    }
}
