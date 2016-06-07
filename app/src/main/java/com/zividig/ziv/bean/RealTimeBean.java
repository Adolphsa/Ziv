package com.zividig.ziv.bean;

import java.util.List;

/**
 * 实时预览从服务器上获取的Json
 * Created by Administrator on 2016-06-04.
 */
public class RealTimeBean {


    /**
     * cmd : snap
     * picnum : 1
     * picinfo : [{"url":"http://dev.caowei.name/mytest/uploadtest/upload_dir/1234567890123456789/snap_ch0_20160603114554110787.jpg"}]
     */

    private String cmd;
    private int picnum;
    /**
     * url : http://dev.caowei.name/mytest/uploadtest/upload_dir/1234567890123456789/snap_ch0_20160603114554110787.jpg
     */

    private List<PicinfoBean> picinfo;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getPicnum() {
        return picnum;
    }

    public void setPicnum(int picnum) {
        this.picnum = picnum;
    }

    public List<PicinfoBean> getPicinfo() {
        return picinfo;
    }

    public void setPicinfo(List<PicinfoBean> picinfo) {
        this.picinfo = picinfo;
    }

    public static class PicinfoBean {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
