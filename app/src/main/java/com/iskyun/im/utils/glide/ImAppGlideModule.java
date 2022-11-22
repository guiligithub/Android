package com.iskyun.im.utils.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.iskyun.im.ImLiveApp;

@GlideModule
public class ImAppGlideModule extends AppGlideModule {

    private long diskSize = 100*1024*1024;

    /**
     * 设置内存缓存大小
     */
    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(ImLiveApp.get()).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int defaultArrayPoolSize = calculator.getArrayPoolSizeInBytes();
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565));

        String cachePath = ImLiveApp.get().getExternalCacheDir().getAbsolutePath();
        builder.setDiskCache( new DiskLruCacheFactory(cachePath, diskSize) );
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "cache", diskSize));
        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize/2));
        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize/2));
        builder.setArrayPool(new LruArrayPool(defaultArrayPoolSize/2));


    }

    /**
     * 关闭解析AndroidManifest
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}

