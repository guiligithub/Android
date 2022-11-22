package com.iskyun.im.data.bean;

import java.io.Serializable;

public class Guide implements Serializable {
    public int imgPath;//图片地址
    public String imgName;

    public Guide() {
    }

    public Guide(int imgPath, String imgName) {
        this.imgPath = imgPath;
        this.imgName = imgName;
    }

    public int getImgPath() {
        return imgPath;
    }

    public void setImgPath(int imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }


    @Override
    public String toString() {
        return "Visitor{" +
                "imgPath='" + imgPath + '\'' +
                ", imgName='" + imgName + '\'' +
                '}';
    }

}

