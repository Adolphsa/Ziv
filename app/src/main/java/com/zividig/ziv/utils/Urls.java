package com.zividig.ziv.utils;

/**
 * Created by adolph
 * on 2016-09-26.
 */

public class Urls {

    //注册
    public static final String REGISTER_URL = "http://api.caowei.name/user";

    //获取验证码GET_YZM_URL + "/" + fdUser
    public static final String GET_YZM_URL = "http://api.caowei.name/sms";

    //登录
    public static final String LOGIN_URL = "http://api.caowei.name/login";

    //设备绑定
    public static final String URL_BIND_DEVICE = "http://api.caowei.name/devicebind/";

    //获取设备列表  GET_DEVICE_LIST+ "/" + user
    public static final String GET_DEVICE_LIST = "http://api.caowei.name/devicelist";

    //图片抓拍
    public static final String URL_PIC_SNAP = "http://120.24.174.213:9501/api/snap";

    //在设备wifi下获取设备信息
    public static final String GET_DEVICE_INFO = "http://192.168.1.1/api/getdevinfo";

    //请求视频
    public static final String REQUEST_VIDEO = "http://120.24.174.213:9501/api/requestrtspstream";

    //主机唤醒
    public static final String DEVICE_WAKEUP = "http://120.24.174.213:9501/api/wakeupdevice";

}
