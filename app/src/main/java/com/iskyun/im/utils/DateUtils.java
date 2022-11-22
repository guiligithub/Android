package com.iskyun.im.utils;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.iskyun.im.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String YMDHMS = "yyyy-MM-dd-HH-mm-ss";
    public static final String YMDHM = "yyyy-MM-dd-HH-mm";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm:ss";
    public static final String MD_HM = "MM-dd HH:mm";

    /**
     * 当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return DateFormat.format(YMD_HM, Calendar.getInstance(Locale.CHINA)).toString();
    }

    /**
     * 当前时间 N天后
     *
     * @param distanceDay
     * @return
     */
    public static String getTimeByDay(int distanceDay) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(YMD_HM);
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance(Locale.CHINA);
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        try {
            Date endDate = sdf.parse(sdf.format(date.getTime()));
            if (endDate != null) {
                return sdf.format(endDate);
            }
        } catch (ParseException e) {
            LogUtils.e("ParseException:" + e.getMessage());
        }
        return null;
    }

    /**
     * startTime N天后
     * @param startTime
     * @param distanceDay
     * @return
     */
    public static String getTimeByDay(String startTime, int distanceDay) {
        if(TextUtils.isEmpty(startTime)){
            return "";
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(YMD_HM);
        try {
            Date startDate = sdf.parse(startTime);
            Calendar date = Calendar.getInstance(Locale.CHINA);
            if (startDate != null) {
                date.setTime(startDate);
            }
            date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
            Date endDate = sdf.parse(sdf.format(date.getTime()));
            if (endDate != null) {
                return sdf.format(endDate);
            }
        } catch (ParseException e) {
            LogUtils.e("ParseException:" + e.getMessage());
        }
        return null;
    }

    public static String getRelativeTimeSpanString(String dateFormatTime) {
        if (TextUtils.isEmpty(dateFormatTime))
            return "";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(YMD_HM);
        try {
            Date endDate = sdf.parse(dateFormatTime);
            String time = android.text.format.DateUtils.getRelativeTimeSpanString(endDate.getTime()).toString();
            if (time.startsWith("0")) {
                time.replace("0", "1");
            }
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当前时间与目标时间的比较
     *
     * @param anotherDate
     * @return true 在目标时间前
     */
    public static boolean currentDateCompareTo(String anotherDate) {
        boolean isAfter = false;
        if (!TextUtils.isEmpty(anotherDate)) {
            Date currentDate = Calendar.getInstance(Locale.CHINA).getTime();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YMD_HM);
            try {
                Date expireDate = sdf.parse(anotherDate);
                isAfter = currentDate.before(expireDate);
            } catch (ParseException e) {
                LogUtils.e(e.getMessage());
            }
        }
        return isAfter;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateNoYear(String formatTime) {
        if (TextUtils.isEmpty(formatTime)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YMD_HM);
            Date endDate = sdf.parse(formatTime);
            SimpleDateFormat sdf1 = new SimpleDateFormat(MD_HM);
            return sdf1.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatTime;
    }

    /**
     * vip 时间转化
     *
     * @param vipTime
     * @return
     */
    public static String formatVipTime(int vipTime) {
        String content;
        String day = DisplayUtils.getString(R.string.day);
        String month = DisplayUtils.getString(R.string.month);
        if (vipTime < 30) {
            content = vipTime + day;
        } else {
            content = vipTime / 30 + month;
        }
        return content;
    }
}
