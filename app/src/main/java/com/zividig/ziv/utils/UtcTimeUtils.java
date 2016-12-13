package com.zividig.ziv.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by adolph
 * on 2016-10-19.
 *
 * UTC时间转换类
 */

public class UtcTimeUtils {

    public static Calendar DateTimeToUTC(String dateTime) {

        Calendar calendar = Calendar.getInstance();

        Date d1 = null;//定义起始日期
        try {
            d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");

        String yearStr = sdf0.format(d1); // 年
        String monthStr = sdf1.format(d1); // 月
        String dayStr = sdf2.format(d1); // 日

        String hourStr = sdf3.format(d1); // 时
        String minuteStr = sdf4.format(d1); // 分

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute,0);
        return calendar;




    }

    /***
     * 获取时间和日期
     *
     * @return string
     */
    public static String getDateAndTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        System.out.println(date);
        return date;
    }

    public static String getTimestamp(){

        return System.currentTimeMillis()/1000 + "";
    }
}
