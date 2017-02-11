package com.zividig.ziv.bean;

import java.util.List;

/**
 * Created by adolph
 * on 2017-02-11.
 */

public class MessageBean {

    /**
     * status : 200
     * data : [{"id":3,"type":"shake","lat":22.623213,"lon":114.028308,"address":"广东省深圳市宝安区民旺路","address_desc":"深圳市宝安区龙华民治街道梅龙路七星商业广场附近31米","time":"2017-02-10 18:36:05"},{"id":"2","type":"shake","lat":22.623213,"lon":114.028308,"address":"广东省深圳市宝安区民旺路","address_desc":"深圳市宝安区龙华民治街道梅龙路七星商业广场附近31米","time":"2017-02-10 18:35:56"}]
     */

    private int status;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageBean2{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }

    public static class DataBean {
        /**
         * id : 3
         * type : shake
         * lat : 22.623213
         * lon : 114.028308
         * address : 广东省深圳市宝安区民旺路
         * address_desc : 深圳市宝安区龙华民治街道梅龙路七星商业广场附近31米
         * time : 2017-02-10 18:36:05
         */

        private int id;
        private String type;
        private double lat;
        private double lon;
        private String address;
        private String address_desc;
        private String time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress_desc() {
            return address_desc;
        }

        public void setAddress_desc(String address_desc) {
            this.address_desc = address_desc;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }


        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", lat=" + lat +
                    ", lon=" + lon +
                    ", address='" + address + '\'' +
                    ", address_desc='" + address_desc + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }
}
