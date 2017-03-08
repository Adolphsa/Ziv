package com.zividig.ziv.bean;

/**
 * 实时预览从服务器上获取的Json
 * Created by Administrator on 2016-06-04.
 */
public class RealTimeBean {


    /**
     * status : 200
     * url :
     * key : 1488882230
     * message : bbb
     */

    private int status;
    private String url;
    private String key;
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
