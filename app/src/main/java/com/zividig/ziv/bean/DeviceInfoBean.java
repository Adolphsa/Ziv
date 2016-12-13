package com.zividig.ziv.bean;

import java.util.List;

/**
 * 登录信息
 * Created by Administrator on 2016-06-14.
 */
public class DeviceInfoBean {

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * userid : 13480995624
     * devnum : 1
     * devinfo : [{"devid":"1234567890123456789","devtype":"2 channel MDVR"}]
     */

    private String userid;
    private int devnum;
    /**
     * devid : 1234567890123456789
     * devtype : 2 channel MDVR
     */

    private List<DevinfoBean> devinfo;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getDevnum() {
        return devnum;
    }

    public void setDevnum(int devnum) {
        this.devnum = devnum;
    }

    public List<DevinfoBean> getDevinfo() {
        return devinfo;
    }

    public void setDevinfo(List<DevinfoBean> devinfo) {
        this.devinfo = devinfo;
    }

    public static class DevinfoBean {
        private String devid;
        private String devtype;

        public String getDevid() {
            return devid;
        }

        public void setDevid(String devid) {
            this.devid = devid;
        }

        public String getDevtype() {
            return devtype;
        }

        public void setDevtype(String devtype) {
            this.devtype = devtype;
        }
    }

}
