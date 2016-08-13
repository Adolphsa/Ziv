package com.zividig.ziv.bean;

/**
 * 视频流信息
 * Created by Administrator on 2016-08-13.
 */
public class VideoInfoBean {

    /**
     * cmd : requestRtsp
     * error : 200
     * errorStr : OK
     * url : rtsp://120.25.80.80:8554/live_1234567890123456789.sdp
     */

    private String cmd;
    private int error;
    private String errorStr;
    private String url;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public void setErrorStr(String errorStr) {
        this.errorStr = errorStr;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
