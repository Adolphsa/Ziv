package com.zividig.ziv.utils;

import org.xutils.http.RequestParams;

import java.util.Random;

/**
 * Created by adolph
 * on 2016-12-12.
 */

public class HttpParamsUtils {



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
     */
    public static RequestParams setParams(String uri, String timestamp, String noncestr, String signature){

        RequestParams params = new RequestParams(uri);
        params.setAsJsonContent(true);
        params.addBodyParameter(SignatureUtils.SIGNATURE_APP_KEY,Urls.APP_KEY);
        params.addBodyParameter(SignatureUtils.SIGNATURE_TIMESTAMP,timestamp);
        params.addBodyParameter(SignatureUtils.SIGNATURE_NONCESTTR,noncestr);
        params.addBodyParameter(SignatureUtils.SIGNATURE_STRING,signature);

        return params;
    }
}
