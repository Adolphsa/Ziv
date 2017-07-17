package com.zividig.ziv.bean;

import java.util.List;

/**
 * Created by adolph
 * on 2017-07-07.
 */

public class VersionUpdateBean {

    /**
     * versionName : 2.0
     * versionCode : 2
     * description : 2017/06/26最新版本
     * downloadUrl : [{"loadurl":"http://120.25.80.80/~adolph/zivApp/picture/app-ziv-release.apk"},{"loadurl":"http://120.25.80.80/~adolph/zivApp/picture/app-carboloo-release.apk"},{"loadurl":"http://120.25.80.80/~adolph/zivApp/picture/app-ht-release.apk"},{"loadurl":"http://120.25.80.80/~adolph/zivApp/picture/app-smj-release.apk"},{"loadurl":"http://120.25.80.80/~adolph/zivApp/picture/app-careyes-release.apk"}]
     */

    private String versionName;
    private int versionCode;
    private String description;

    private List<DownloadUrlBean> downloadUrl;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DownloadUrlBean> getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(List<DownloadUrlBean> downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public static class DownloadUrlBean {
        /**
         * loadurl : http://120.25.80.80/~adolph/zivApp/picture/app-ziv-release.apk
         */

        private String loadurl;

        public String getLoadurl() {
            return loadurl;
        }

        public void setLoadurl(String loadurl) {
            this.loadurl = loadurl;
        }
    }
}
