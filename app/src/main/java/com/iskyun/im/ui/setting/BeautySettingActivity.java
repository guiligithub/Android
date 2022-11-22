package com.iskyun.im.ui.setting;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import cn.tillusory.sdk.gles.TiGLUtils;
import cn.tillusory.sdk.renderer.TakePictureRenderer;
import cn.tillusory.sdk.renderer.TiShowRenderer;
import cn.tillusory.tiui.TiPanelLayout;

/**
 */
public class BeautySettingActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private String TAG = "BeautySettingActivity";

    private GLSurfaceView glSurfaceView;
    private TiCamera camera;

    private TiShowRenderer showRenderer;
    private TakePictureRenderer takePictureRenderer;

    private SurfaceTexture surfaceTexture;
    private int oesTextureId;

    /**
     * 相机采集的宽高
     */
    private int imageWidth = 1280, imageHeight = 720;

    private boolean isFrontCamera = true;
    private TiRotation tiRotation;
    private int cameraId;

    private boolean isTakePicture = false;
    private int pictureWidth = 720, pictureHeight = 1280;
    private String picturePath;
    private HandlerThread pictureHandlerThread;
    private Handler pictureHandler;

    /**
     * 页面显示的宽高
     */
    private int surfaceWidth, surfaceHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setContentView(glSurfaceView);

        addContentView(new TiPanelLayout(this).init(TiSDKManager.getInstance()),
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


        pictureHandlerThread = new HandlerThread("TakePicture");
        pictureHandlerThread.start();
        pictureHandler = new Handler(pictureHandlerThread.getLooper());
        camera = new TiCamera(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TiSDKManager.getInstance().destroy();
        pictureHandlerThread.quit();
        camera.releaseCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        Log.i(TAG, "onSurfaceChanged width = " + width + ", height = " + height);
        surfaceWidth = width;
        surfaceHeight = height;

        showRenderer = new TiShowRenderer(surfaceWidth, surfaceHeight);
        showRenderer.create(isFrontCamera);

        takePictureRenderer = new TakePictureRenderer(pictureWidth, pictureHeight);
        takePictureRenderer.create(isFrontCamera);

        oesTextureId = TiGLUtils.getExternalOESTextureID();
        surfaceTexture = new SurfaceTexture(oesTextureId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                glSurfaceView.requestRender();
            }
        });

        cameraId = isFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
        tiRotation = isFrontCamera ? TiRotation.CLOCKWISE_ROTATION_270 : TiRotation.CLOCKWISE_ROTATION_90;
        camera.openCamera(cameraId, imageWidth, imageHeight);
        camera.setPreviewSurface(surfaceTexture);
        camera.startPreview();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG, "onDrawFrame");
        int textureId = TiSDKManager.getInstance().renderOESTexture(oesTextureId, imageWidth, imageHeight, tiRotation, isFrontCamera);
        showRenderer.render(textureId);
        surfaceTexture.updateTexImage();
    }


}
