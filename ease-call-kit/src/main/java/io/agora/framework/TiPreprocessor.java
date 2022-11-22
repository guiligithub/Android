package io.agora.framework;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import io.agora.capture.framework.modules.channels.VideoChannel;
import io.agora.capture.framework.modules.processors.IPreprocessor;
import io.agora.capture.video.camera.VideoCaptureFrame;

public class TiPreprocessor implements IPreprocessor {
    private final static String TAG = TiPreprocessor.class.getSimpleName();

    private Context mContext;
    private boolean mEnabled;

    public TiPreprocessor(Context context) {
        mContext = context;
        mEnabled = true;
    }

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame outFrame, VideoChannel.ChannelContext context) {
        if (!mEnabled) {
            return outFrame;
        }
        //todo --- tillusory start ---
        outFrame.textureId = TiSDKManager.getInstance().renderOESTexture(outFrame.textureId,
                outFrame.format.getWidth(),
                outFrame.format.getHeight(),
                TiRotation.fromValue(outFrame.rotation),
                outFrame.mirrored);

        // The texture is transformed to texture2D by beauty module.
        outFrame.format.setTexFormat(GLES20.GL_TEXTURE_2D);
        //todo --- tillusory end ---
        return outFrame;
    }

    @Override
    public void initPreprocessor() {
        // only call once when app launched
        Log.e(TAG, "initPreprocessor: ");
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext context) {
        // not called
        Log.d(TAG, "releasePreprocessor: ");
    }

}
