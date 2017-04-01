package com.zividig.ziv.bean;

import android.os.Parcel;
import android.os.Parcelable;

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

    public static class DataBean implements Parcelable {
        /**
         * id : 3
         * title: 报警类型信息
         * type : shake
         * lat : 22.623213
         * lon : 114.028308
         * address : 广东省深圳市宝安区民旺路
         * address_desc : 深圳市宝安区龙华民治街道梅龙路七星商业广场附近31米
         * time : 2017-02-10 18:36:05
         */

        private int id;
        private String title;
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

        public String getTitle() {return title;}

        public void setTitle(String title) {title = title;}

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.title);
            dest.writeString(this.type);
            dest.writeDouble(this.lat);
            dest.writeDouble(this.lon);
            dest.writeString(this.address);
            dest.writeString(this.address_desc);
            dest.writeString(this.time);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.id = in.readInt();
            this.title = in.readString();
            this.type = in.readString();
            this.lat = in.readDouble();
            this.lon = in.readDouble();
            this.address = in.readString();
            this.address_desc = in.readString();
            this.time = in.readString();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }
}
