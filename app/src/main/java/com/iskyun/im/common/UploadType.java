package com.iskyun.im.common;

public enum UploadType {
    CAMERA_VIDEO(0x100),//相机视频
    CAMERA_PIC(0x101),//相机照片
    ALBUM(0x102),//相册
    VIDEO(0x103),//视频
    CROP(0x104);//裁剪

    int value;

    UploadType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
