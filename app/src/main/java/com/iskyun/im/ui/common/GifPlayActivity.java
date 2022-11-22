package com.iskyun.im.ui.common;

import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.common.GifListener;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GifPlayActivity extends AppCompatActivity implements GifListener {

    public static final String URL = "url";

    public static void launcher(String url){
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(),bundle, GifPlayActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        ImageView imageView = findViewById(R.id.imageView2);
        if (getIntent() != null && getIntent().getExtras() != null) {
            String url = getIntent().getExtras().getString(URL);
            //String url = "https://huacao-api.oss-cn-hangzhou.aliyuncs.com/upload/huacao/2022-09-06%E7%81%AB%E7%AE%AD%E9%A2%84%E8%A7%88.gif";
            loadGif(url, imageView, this);
        }
        setFinishOnTouchOutside(true);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int width = DisplayUtils.getWidthPixels()- 2*DisplayUtils.dp2px(24);
        lp.width = width;
        lp.height = width;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
    }

    @Override
    public void gifPlayComplete() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void loadGif(String url, ImageView viewTarget, GifListener gifListener) {
        Glide.with(this)
                .setDefaultRequestOptions(new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888).set(GifOptions.DECODE_FORMAT,
                        DecodeFormat.PREFER_ARGB_8888))
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Field gifStateField = GifDrawable.class.getDeclaredField("state");
                            gifStateField.setAccessible(true);
                            Class<?> gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                            Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                            gifFrameLoaderField.setAccessible(true);
                            Class<?> gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                            Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
                            gifDecoderField.setAccessible(true);
                            Class<?> gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
                            Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)));
                            Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
                            getDelayMethod.setAccessible(true);
                            //设置只播放一次
                            resource.setLoopCount(1);
                            //获得总帧数
                            int count = resource.getFrameCount();
                            int delay = 0;
                            for (int i = 0; i < count; i++) {
                                //计算每一帧所需要的时间进行累加
                                delay += (int) getDelayMethod.invoke(gifDecoder, i);
                            }
                            viewTarget.postDelayed(() -> {
                                if (gifListener != null) {
                                    gifListener.gifPlayComplete();
                                }
                            }, delay);
                        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException e) {
                            LogUtils.e(e.getMessage());
                        }
                        return false;
                    }
                })
                .into(viewTarget);
    }
}