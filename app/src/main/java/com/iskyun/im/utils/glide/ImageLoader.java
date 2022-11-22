package com.iskyun.im.utils.glide;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.iskyun.im.R;
import com.iskyun.im.utils.DisplayUtils;

public final class ImageLoader {

    private static final String KEY_MEMORY = "com.bumptech.glide.load.model.stream.HttpGlideUrlLoader.Timeout";
    private static final float SIZE_MULTIPLIER = 0.3f;
    private static final int TIMEOUT_MS = 16000;
    private final DrawableTransitionOptions normalTransitionOptions = new DrawableTransitionOptions()
            .crossFade();
    private static final Option<Integer> TIMEOUT_OPTION =
            Option.memory(KEY_MEMORY, TIMEOUT_MS);
    private static volatile ImageLoader INSTANCE;

    private final RequestOptions options;
    private final RequestOptions transparentOptions;
    private RequestBuilder builder;

    private ImageLoader() {
        options = new RequestOptions()
                .placeholder(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true);

        transparentOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false);
    }

    public static ImageLoader get() {
        if (INSTANCE == null) {
            synchronized (ImageLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImageLoader();
                }
            }
        }
        return INSTANCE;
    }


    public void load(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .apply(options)
                .into(imageView);
    }

    public void loadTransparentImage(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .apply(transparentOptions)
                .into(imageView);
    }

    public void load(ImageView imageView, @DrawableRes int id) {
        GlideApp.with(imageView.getContext())
                .load(id)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .apply(options)
                .into(imageView);
    }

    public void load(ImageView imageView, Uri uri) {
        GlideApp.with(imageView.getContext())
                .load(uri)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .apply(options)
                .into(imageView);
    }


    public void load(ImageView imageView, String url, RequestListener<Drawable> listener) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions).dontAnimate()
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        listener.onLoadFailed(e, model, target, isFirstResource);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onResourceReady(resource, model, target, dataSource, isFirstResource);
                        return false;
                    }
                })
                .into(imageView);
    }

    public void load(ImageView imageView, int resId, RequestListener<Drawable> listener) {
        GlideApp.with(imageView.getContext())
                .load(resId)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        listener.onLoadFailed(e, model, target, isFirstResource);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        listener.onResourceReady(resource, model, target, dataSource,
                                isFirstResource);
                        return false;
                    }
                })
                .transition(normalTransitionOptions)
                .into(imageView);
    }

    public void loadThumb(ImageView imageView, String url) {
        GlideRequest<Drawable> builder = GlideApp.with(imageView.getContext()).load(url);
        builder.set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .apply(options)
                .thumbnail(builder)
                .into(imageView);
    }

    public void loadCropCircle(ImageView imageView,Uri uri){
        GlideApp.with(imageView.getContext())
                .load(uri)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .transform(new CenterCrop(),new CircleCrop())
                .apply(options)
                .into(imageView);
    }

    /**
     * 圆形图片
     * @param imageView
     * @param url
     */
    public void loadCropCircle(ImageView imageView, String url) {
        GlideApp.with(imageView.getContext())
                .load(url)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .transform(new CenterCrop(),new CircleCrop())
                .apply(options)
                .into(imageView);
    }

    /**
     * 圆角图片
     * @param imageView
     * @param url
     */
    public void loadRoundedCorners(ImageView imageView, String url,int roundingRadius){
        GlideApp.with(imageView.getContext())
                .load(url)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .transform(new CenterCrop(),new RoundedCorners(roundingRadius))
                .apply(options)
                .into(imageView);
    }


    public void loadRoundedCorners(ImageView imageView, Uri uri,int roundingRadius){
        GlideApp.with(imageView.getContext())
                .load(uri)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions)
                .transform(new CenterCrop(),new RoundedCorners(roundingRadius))
                .apply(options)
                .into(imageView);
    }


    public void loadRoundedCorners(ImageView imageView, String url){
        loadRoundedCorners(imageView, url, (int) DisplayUtils.getDimension(R.dimen.rounded_radius));
    }

    public void loadRoundedCorners(ImageView imageView, Uri uri){
        loadRoundedCorners(imageView, uri, (int) DisplayUtils.getDimension(R.dimen.rounded_radius));
    }

    public void loadCropCircle(ImageView imageView, int resId) {
        GlideApp.with(imageView.getContext())
                .load(resId)
                .set(TIMEOUT_OPTION, TIMEOUT_MS)
                .transition(normalTransitionOptions).dontAnimate()
                .transform(new CenterCrop(),new CircleCrop())
                .apply(options)
                .into(imageView);
    }
}
