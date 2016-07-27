package com.zividig.ziv.utils;

import java.security.MessageDigest;

/**
 * MD5加密
 * Created by Administrator on 2016-07-27.
 */
public class MD5 {
    public static String getMD5(String val)throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte [] m = md5.digest(); //加密
        return getString(m);
    }
    private  static String getString(byte[] m){
        StringBuilder sb = new StringBuilder(m.length * 2);
        for(byte b: m){
            sb.append(Integer.toHexString((b & 0xf0) >>> 4));
            sb.append(Integer.toHexString(b & 0x0f));
        }
        return sb.toString();
    }
}
