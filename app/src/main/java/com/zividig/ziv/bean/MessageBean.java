package com.zividig.ziv.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adolph
 * on 2016-11-04.
 */

public class MessageBean implements Parcelable {

    /**
     * alarmType : shock
     * alarmContent : thie is a shock alarm
     * alarmTime : 2016-11-04 12:04
     */

    private String alarmType;
    private String alarmContent;
    private String alarmTime;

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alarmType);
        dest.writeString(this.alarmContent);
        dest.writeString(this.alarmTime);
    }

    public MessageBean() {
    }

    protected MessageBean(Parcel in) {
        this.alarmType = in.readString();
        this.alarmContent = in.readString();
        this.alarmTime = in.readString();
    }

    public static final Parcelable.Creator<MessageBean> CREATOR = new Parcelable.Creator<MessageBean>() {
        @Override
        public MessageBean createFromParcel(Parcel source) {
            return new MessageBean(source);
        }

        @Override
        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };
}
