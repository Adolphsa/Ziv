package com.zividig.ziv.bean;

/**
 * 设备状态信息
 * Created by adolph
 * on 2017-03-02.
 */

public class DeviceStateInfoBean {


    /**
     * status : 200
     * info : {"workmode":"OFF","voltage":0,"csq":0}
     * message : aaa
     */

    private int status;
    private InfoBean info;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class InfoBean {
        /**
         * workmode : OFF
         * voltage : 0
         * csq : 0
         */

        private String workmode;
        private String voltage;
        private String csq;

        public String getWorkmode() {
            return workmode;
        }

        public void setWorkmode(String workmode) {
            this.workmode = workmode;
        }

        public String getVoltage() {
            return voltage;
        }

        public void setVoltage(String voltage) {
            this.voltage = voltage;
        }

        public String getCsq() {
            return csq;
        }

        public void setCsq(String csq) {
            this.csq = csq;
        }
    }
}
