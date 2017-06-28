package com.zividig.ziv.rxjava.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * 登录请求body
 * Created by adolph
 * on 2017-02-28.
 */

public class LoginBody {

    @SerializedName("username")
    public String userName;

    @SerializedName("password")
    public String password;

    @SerializedName("getuiid")
    public String getuiId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGetuiId() {
        return getuiId;
    }

    public void setGetuiId(String getuiId) {
        this.getuiId = getuiId;
    }
}
