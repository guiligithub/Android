package com.iskyun.im.data.bean;

import java.util.ArrayList;

public class ImageBannerBean {
    private String picture;
    public ImageBannerBean(String picture){
        this.picture = picture;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
}
