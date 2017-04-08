package com.zividig.ziv.rxjava.model;

/**
 * Created by adolph
 * on 2017-04-06.
 */

public class VideoResponse {

    /**
     * status : 200
     * url : aaa
     * message : bbb
     */

    private int status;
    private String url;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
