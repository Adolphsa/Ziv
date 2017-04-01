package com.zividig.ziv.rxjava.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adolph
 * on 2017-03-15.
 */

public class SnapBody {

    @SerializedName("devid")
    public String devid;

    @SerializedName("key")
    public String imageKey;

    @SerializedName("token")
    public String token;

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
}
