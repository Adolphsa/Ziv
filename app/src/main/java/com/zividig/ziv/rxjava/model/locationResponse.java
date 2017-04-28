package com.zividig.ziv.rxjava.model;

/**
 * Created by adolph
 * on 2017-03-17.
 */

public class LocationResponse {

    /**
     * status : 200
     * gps : {"ti":1489741363,"lat":22.548904,"lon":113.920921,"spd":0,"hd":53,"alt":351}
     */

    private int status;
    private GpsBean gps;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public GpsBean getGps() {
        return gps;
    }

    public void setGps(GpsBean gps) {
        this.gps = gps;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class GpsBean {
        /**
         * ti : 1489741363
         * lat : 22.548904
         * lon : 113.920921
         * spd : 0
         * hd : 53
         * alt : 351
         */

        private String ti;
        private double lat;
        private double lon;
        private double spd;
        private int hd;
        private int alt;

        public String getTi() {
            return ti;
        }

        public void setTi(String ti) {
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

        @Override
        public String toString() {
            return "GpsBean{" +
                    "ti='" + ti + '\'' +
                    ", lat=" + lat +
                    ", lon=" + lon +
                    ", spd=" + spd +
                    ", hd=" + hd +
                    ", alt=" + alt +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LocationResponse{" +
                "status=" + status +
                ", gps=" + gps +
                ", message='" + message + '\'' +
                '}';
    }
}
