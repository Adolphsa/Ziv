package com.zividig.ziv.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 位置数据
 * Created by Administrator on 2016-06-13.
 */
public class LocationBean implements Parcelable{
    /**
     * ti : 2016-06-13 03:47:30
     * lat : 22.549505
     * lon : 113.920738
     * spd : 0
     * hd : 351
     * alt : 64
     */

    private String ti;
    private double lat;
    private double lon;
    private double spd;
    private String hd;
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

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public int getAlt() {
        return alt;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ti);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeDouble(this.spd);
        dest.writeString(this.hd);
        dest.writeInt(this.alt);
    }

    public LocationBean() {
    }

    protected LocationBean(Parcel in) {
        this.ti = in.readString();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.spd = in.readInt();
        this.hd = in.readString();
        this.alt = in.readInt();
    }

    public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
        @Override
        public LocationBean createFromParcel(Parcel source) {
            return new LocationBean(source);
        }

        @Override
        public LocationBean[] newArray(int size) {
            return new LocationBean[size];
        }
    };
}
