package cn.hnit.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 谢武科
 * @date 2020/12/2 22:35
 */
public class MonthUtils {

    /**
     * yyyy-MM形式
     *
     * @return
     */
    public static String currentMonth() {
        Date date = new Date();
        return DateUtil.formatDate(date, "yyyy-MM");
    }


    /**
     * 得到上个月
     *
     * @return
     */
    public static String getLastMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
        date = calendar.getTime();
        String accDate = format.format(date);
        return accDate;
    }

    /**
     * 得到下个月1号的秒数
     *
     * @return
     */
    public static long differNextMonthOneSeconds() {
        Date date = new Date();
        long seconds = DateUtil.compareDateSeconds(date, nextMonthOneDay());
        return Math.abs(seconds);
    }

    public static Date nextMonthOneDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MINUTE, 1);
        return calendar.getTime();
    }

    /**
     * 当月第一天
     *
     * @return
     */
    public static Date firstDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

    public static String currentMonth(Date date) {
        return DateUtil.formatDate(date, "yyyy-MM");
    }
}
