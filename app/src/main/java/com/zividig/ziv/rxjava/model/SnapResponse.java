package com.zividig.ziv.rxjava.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adolph
 * on 2017-03-15.
 */

public class SnapResponse {

    /**
     * status : 200
     * url : http
     * key : 1489559305
     * message : aa
     */

    @SerializedName("status")
    private int status;

    @SerializedName("url")
    private String url;

    @SerializedName("key")
    private String key;

    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
