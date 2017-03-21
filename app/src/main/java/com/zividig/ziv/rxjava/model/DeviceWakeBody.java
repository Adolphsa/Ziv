package com.zividig.ziv.rxjava.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adolph
 * on 2017-03-20.
 */

public class DeviceWakeBody {

    @SerializedName("devid")
    public String devid;

    @SerializedName("token")
    public String token;

    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
