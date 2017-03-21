package com.zividig.ziv.rxjava.model;

import com.google.gson.annotations.SerializedName;

/**
 * 设备状态请求体
 * Created by adolph
 * on 2017-03-13.
 */

public class DeviceStateBody {

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
