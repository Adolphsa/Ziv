package com.zividig.ziv.bean;

import java.util.List;

/**
 * 轨迹数据
 * Created by Administrator on 2016-06-24.
 */
public class MapTrackBean {

    /**
     * begin : 111
     * end : 999999999999
     * itemnum : 14
     * locationdata : [{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.50732,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.50732,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.50732,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.507321,"lon":114.233112,"spd":0,"hd":309,"alt":33},{"ti":2016,"lat":30.50732,"lon":114.233112,"spd":0,"hd":309,"alt":33}]
     */

    private String begin; //开始时间
    private String end;     //结束时间
    private int itemnum;  //地图数据的个数
    /**
     * ti : 2016
     * lat : 30.507321
     * lon : 114.233112
     * spd : 0
     * hd : 309
     * alt : 33
     */

    private List<LocationdataBean> locationdata;

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
        private int ti;
        private double lat;
        private double lon;
        private float spd;
        private int hd;
        private int alt;

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

        public float getSpd() {
            return spd;
        }

        public void setSpd(float spd) {
            this.spd = spd;
        }

        public int getHd() {
            return hd;
        }

        public void setHd(int hd) {
            this.hd = hd;
        }

        public int getAlt() {
            return alt;
        }

        public void setAlt(int alt) {
            this.alt = alt;
        }
    }
}
