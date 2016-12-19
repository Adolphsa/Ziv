package com.zividig.ndk_test.weizhang.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adolph
 * on 2016-12-14.
 */

public class CarInfo implements Parcelable {

    private String abbr;
    private String city;
    private String cityCode;
    private String isEngine;
    private String engineCode;
    private String isClassa;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getIsEngine() {
        return isEngine;
    }

    public void setIsEngine(String isEngine) {
        this.isEngine = isEngine;
    }

    public String getEngineCode() {
        return engineCode;
    }

    public void setEngineCode(String engineCode) {
        this.engineCode = engineCode;
    }

    public String getIsClassa() {
        return isClassa;
    }

    public void setIsClassa(String isClassa) {
        this.isClassa = isClassa;
    }

    String classCode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.abbr);
        dest.writeString(this.city);
        dest.writeString(this.cityCode);
        dest.writeString(this.isEngine);
        dest.writeString(this.engineCode);
        dest.writeString(this.isClassa);
        dest.writeString(this.classCode);
    }

    public CarInfo() {
    }

    protected CarInfo(Parcel in) {
        this.abbr = in.readString();
        this.city = in.readString();
        this.cityCode = in.readString();
        this.isEngine = in.readString();
        this.engineCode = in.readString();
        this.isClassa = in.readString();
        this.classCode = in.readString();
    }

    public static final Parcelable.Creator<CarInfo> CREATOR = new Parcelable.Creator<CarInfo>() {
        @Override
        public CarInfo createFromParcel(Parcel source) {
            return new CarInfo(source);
        }

        @Override
        public CarInfo[] newArray(int size) {
            return new CarInfo[size];
        }
    };
}
