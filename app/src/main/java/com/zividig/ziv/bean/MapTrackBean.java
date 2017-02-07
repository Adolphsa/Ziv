package com.zividig.ziv.bean;

import java.util.List;

/**
 * 轨迹数据
 * Created by Administrator on 2016-06-24.
 */
public class MapTrackBean {


    /**
     * status : 200
     * begin : 2017-01-19 18:10:31
     * end : 2017-01-20 18:10:31
     * itemnum : 2704
     * locationdata : [{"ti":1484895292,"lat":22.549794,"lon":113.920605,"spd":0,"hd":0,"alt":32}]
     */

    private int status;
    private String begin;
    private String end;
    private int itemnum;
    private List<LocationdataBean> locationdata;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getItemnum() {
        return itemnum;
    }

    public void setItemnum(int itemnum) {
        this.itemnum = itemnum;
    }

    public List<LocationdataBean> getLocationdata() {
        return locationdata;
    }

    public void setLocationdata(List<LocationdataBean> locationdata) {
        this.locationdata = locationdata;
    }

    public static class LocationdataBean {
        /**
         * ti : 1484895292
         * lat : 22.549794
         * lon : 113.920605
         * spd : 0
         * hd : 0
         * alt : 32
         */

        private int ti;
        private double lat;
        private double lon;
        private double spd;
        private double hd;
        private double alt;

        public int getTi() {
            return ti;
        }

        public void setTi(int ti) {
            this.ti = ti;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getSpd() {
            return spd;
        }

        public void setSpd(double spd) {
            this.spd = spd;
        }

        public double getHd() {
            return hd;
        }

        public void setHd(double hd) {
            this.hd = hd;
        }

        public double getAlt() {
            return alt;
        }

        public void setAlt(double alt) {
            this.alt = alt;
        }
    }
}
