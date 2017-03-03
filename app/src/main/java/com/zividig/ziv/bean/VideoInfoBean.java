package com.zividig.ziv.bean;

/**
 * 视频流信息
 * Created by Administrator on 2016-08-13.
 */
public class VideoInfoBean {

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
