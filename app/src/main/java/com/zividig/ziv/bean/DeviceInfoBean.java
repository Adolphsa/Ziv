package com.zividig.ziv.bean;

import java.util.List;

/**
 * 登录信息
 * Created by Administrator on 2016-06-14.
 */
public class DeviceInfoBean {


    /**
     * status : 200
     * userid : 13480995624
     * devnum : 1
     * devinfo : [{"devid":"ZIV3C00010000AD0849","devtype":null,"alias":null,"carid":null}]
     */

    private int status;
    private String userid;
    private int devnum;
    private List<DevinfoBean> devinfo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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
        /**
         * devid : ZIV3C00010000AD0849
         * devtype : null
         * alias : null
         * carid : null
         */

        private String devid;
        private String devtype;
        private String alias;
        private String carid;

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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getCarid() {
            return carid;
        }

        public void setCarid(String carid) {
            this.carid = carid;
        }
    }
}
