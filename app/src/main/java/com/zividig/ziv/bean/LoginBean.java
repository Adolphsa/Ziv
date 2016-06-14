package com.zividig.ziv.bean;

import java.util.List;

/**
 * 登录信息
 * Created by Administrator on 2016-06-14.
 */
public class LoginBean {

    /**
     * userid : yca
     * devnum : 1
     * devinfo : [{"devid":"1234567890123456789","devtype":0}]
     */

    private String userid;
    private int devnum;
    /**
     * devid : 1234567890123456789
     * devtype : 0
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
        private int devtype;

        public String getDevid() {
            return devid;
        }

        public void setDevid(String devid) {
            this.devid = devid;
        }

        public int getDevtype() {
            return devtype;
        }

        public void setDevtype(int devtype) {
            this.devtype = devtype;
        }
    }
}
