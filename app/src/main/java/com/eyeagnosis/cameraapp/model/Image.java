package com.eyeagnosis.cameraapp.model;

import java.io.Serializable;

public class Image implements Serializable {
    private String picture;
    private Integer serial;

    public Image() {
    }

    public Image(String picture, Integer serial) {
        this.picture = picture;
        this.serial = serial;

    }

    public Integer getSerial() {
        return serial;
    }

    public Integer setSerial(Integer serial) {
        return serial;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}

