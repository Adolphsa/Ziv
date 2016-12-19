package com.zividig.ndk_test.weizhang.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 违章查询结果
 * Created by adolph
 * on 2016-12-15.
 */

public class ViolationResultBean {


    /**
     * resultcode : 200
     * reason : success
     * result : {"province":"GD","city":"GD_SZ","hphm":"粤B4QL82","hpzl":"02","lists":[{"date":"2016","area":"sz","act":"sz","code":"1345","fen":"3","money":"200","handled":"0"}]}
     * error_code : 0
     */

    private String resultcode;
    private String reason;
    private ResultBean result;
    private int error_code;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class ResultBean {
        /**
         * province : GD
         * city : GD_SZ
         * hphm : 粤B4QL82
         * hpzl : 02
         * lists : [{"date":"2016","area":"sz","act":"sz","code":"1345","fen":"3","money":"200","handled":"0"}]
         */

        private String province;
        private String city;
        private String hphm;
        private String hpzl;
        private List<ListsBean> lists;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getHphm() {
            return hphm;
        }

        public void setHphm(String hphm) {
            this.hphm = hphm;
        }

        public String getHpzl() {
            return hpzl;
        }

        public void setHpzl(String hpzl) {
            this.hpzl = hpzl;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean implements Parcelable {
            /**
             * date : 2016
             * area : sz
             * act : sz
             * code : 1345
             * fen : 3
             * money : 200
             * handled : 0
             */

            private String date;
            private String area;
            private String act;
            private String code;
            private String fen;
            private String money;
            private String handled;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getAct() {
                return act;
            }

            public void setAct(String act) {
                this.act = act;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getFen() {
                return fen;
            }

            public void setFen(String fen) {
                this.fen = fen;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public String getHandled() {
                return handled;
            }

            public void setHandled(String handled) {
                this.handled = handled;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.date);
                dest.writeString(this.area);
                dest.writeString(this.act);
                dest.writeString(this.code);
                dest.writeString(this.fen);
                dest.writeString(this.money);
                dest.writeString(this.handled);
            }

            public ListsBean() {
            }

            protected ListsBean(Parcel in) {
                this.date = in.readString();
                this.area = in.readString();
                this.act = in.readString();
                this.code = in.readString();
                this.fen = in.readString();
                this.money = in.readString();
                this.handled = in.readString();
            }

            public static final Parcelable.Creator<ListsBean> CREATOR = new Parcelable.Creator<ListsBean>() {
                @Override
                public ListsBean createFromParcel(Parcel source) {
                    return new ListsBean(source);
                }

                @Override
                public ListsBean[] newArray(int size) {
                    return new ListsBean[size];
                }
            };
        }
    }
}
