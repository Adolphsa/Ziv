package com.zividig.ziv.rxjava.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adolph
 * on 2017-03-15.
 */

public class SnapBody {

   /// channel : 通道
    //resolution : 分辨率


    @SerializedName("devid")
    public String devid;

    @SerializedName("key")
    public String imageKey;

    @SerializedName("token")
    public String token;

    @SerializedName("channel")
    public String channel;

    @SerializedName("resolution")
    public String resolution;

    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
