package com.zividig.ziv.bean;

/**
 * Created by Administrator on 2016-06-06.
 */
public class PictureBean implements Comparable<PictureBean>{

    private Integer picNum;
    private String picUrl;

    public Integer getPicNum() {
        return picNum;
    }

    public void setPicNum(Integer picNum) {
        this.picNum = picNum;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public int compareTo(PictureBean bean) {
        return bean.getPicNum().compareTo(this.getPicNum()); //降序排列
    }
}
