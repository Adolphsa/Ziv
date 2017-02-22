package com.zividig.ziv.utils;

/**
 * Created by adolph
 * on 2016-09-26.
 */

public class Urls {

    public static final int STATUS_CODE_200 = 200;
    public static final int STATUS_CODE_400 = 400;
    public static final int STATUS_CODE_403 = 403;
    public static final int STATUS_CODE_404 = 404;
    public static final int STATUS_CODE_500 = 500;

    public static final String APP_KEY = "1793584B";

    public static final String APP_SECRET = "9D2AD5F5F1DBE68440E4211AD795E584";

    //版本更新
    public static final String UPDATE_VERSION = "http://olpy0ue07.bkt.clouddn.com/ziv_update.json";

    //注册
    public static final String REGISTER_URL = "http://api.zivdigi.com/v1/user/register";

    //获取验证码GET_YZM_URL + "/" + fdUser
    public static final String GET_YZM_URL = "http://api.zivdigi.com/v1/sms/send";

    //登录
    public static final String LOGIN_URL = "http://api.zivdigi.com/v1/user/login";

    //用户退出
    public static final String LOGOUT_URL = "http://api.zivdigi.com/v1/user/logout";

    //设备绑定
    public static final String URL_BIND_DEVICE = "http://api.zivdigi.com/v1/device/bind";

    //获取设备列表  GET_DEVICE_LIST+ "/" + user
    public static final String GET_DEVICE_LIST = "http://api.zivdigi.com/v1/device/mine";

    //图片抓拍
    public static final String URL_PIC_SNAP = "http://120.25.80.80:9501/api/snap";

    //在设备wifi下获取设备信息
    public static final String GET_DEVICE_INFO_WIFI = "http://192.168.1.1/api/getdevinfo";

    //请求视频
    public static final String REQUEST_VIDEO = "http://120.25.80.80:9501/api/requestrtspstream";

    //主机唤醒
    public static final String DEVICE_WAKEUP = "http://120.25.80.80:9501/api/wakeupdevice";

    //获取设备状态
    public static final String DEVICE_STATE = "http://120.25.80.80:9501/api/getdeviceinfo";

    //实时位置
    public static final String REAL_LOCATION = "http://api.zivdigi.com/v1/location/realtime";

    //地图轨迹
    public static final String MAP_DATA_URL = "http://api.zivdigi.com/v1/location/history";

    //设置设备别名和车牌号
    public static final String SETTING_CARID = "http://api.zivdigi.com/v1/device/setting";

    //解绑设备
    public static final String UNBIND_DEVICE = "http://api.zivdigi.com/v1/device/unbind";

    //获取震动报警消息  vibration alarm
    public static final String GET_VIBRATION_ALARM = "http://wx.zivdigi.com/api/device/alarm";

    //设置震动消息免打扰
    public static final String SETTING_ALARM_DO_NOT_DISTURB = "http://api.zivdigi.com/v1/user/alarm";

    //获取电子围栏信息 http://wx.zivdigi.com/api/device/fence/?devid=ZIV3C00010000AE0808
    public static final String GETTING_FENCE_MESSAGE = "http://wx.zivdigi.com/api/device/fence";

    //设置电子围栏信息http://wx.zivdigi.com/api/device/fence/setting?devid=ZIV3C00010000AD0849
    public static final String SETTING_FENCE_MESSAGE = "http://wx.zivdigi.com/api/device/fence/setting";
}
