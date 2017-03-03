package com.zivdigi.helloffmpeg;

import org.xutils.http.RequestParams;

import java.util.Random;

/**
 * Created by adolph
 * on 2016-09-21.
 */

public class ArrayUtil {

    public static final String FF_APP_KEY = "1793584B";

    public static final String FF_APP_SECRET = "9D2AD5F5F1DBE68440E4211AD795E584";

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }

    //获取随机事件
    public static String getTimestamp(){

        return System.currentTimeMillis()/1000 + "";
    }

    /**
     * 生成随机字符串
     * @param length 表示生成字符串的长度
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     *配置请求参数
     * @param uri  地址链接
     * @param timestamp 时间戳
     * @param noncestr  随机字符串
     * @param signature 签名
     * @return  params
     *
     *  public static final String SIGNATURE_APP_KEY = "app_key";
    public static final String SIGNATURE_TIMESTAMP = "timestamp";
    public static final String SIGNATURE_NONCESTTR = "noncestr";
    public static final String SIGNATURE_STRING = "signature";
    public static final String SIGNATURE_TOKEN = "token";
     */
    public static RequestParams setParams(String uri, String timestamp, String noncestr, String signature){

        RequestParams params = new RequestParams(uri);
        params.setAsJsonContent(true);
        params.addBodyParameter("app_key",FF_APP_KEY);
        params.addBodyParameter("timestamp",timestamp);
        params.addBodyParameter("noncestr",noncestr);
        params.addBodyParameter("signature",signature);

        return params;
    }

    //获取签名
    public static String getSinnature(String... args){

        String[] sortStrBefore = FfmpegStringSortUtils.getUrlParam(args);
        String keyStr = "";
        for (String key : sortStrBefore) {
            keyStr += key;
        }
        String signature = keyStr + FF_APP_SECRET;
        return FfmpegMD5.getMD5(signature);
    }
}
