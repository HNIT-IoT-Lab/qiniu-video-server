package cn.hnit.utils;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 谢武科
 * @date 2020/11/24 16:18
 */
public class WeekUtils {

    /**
     * YYYY-10 YYYY表示年 10表示年的第10周
     *
     * @return
     */
    public static String getCurrentWeek() {
        Date date = new Date();
        int count = DateUtil.getYearWeek(DateUtil.formatDate(date, "yyyy-MM-dd"));
        Date weekLastDate = DateUtil.getThisWeekSunday(date);
        String year = DateUtil.formatDate(weekLastDate, "yyyy");
        return year + "-" + count;
    }

    /**
     * @param date
     * @return
     */
    public static String getCurrentWeek(Date date) {
        int count = DateUtil.getYearWeek(DateUtil.formatDate(date, "yyyy-MM-dd"));
        Date weekLastDate = DateUtil.getThisWeekSunday(date);
        String year = DateUtil.formatDate(weekLastDate, "yyyy");
        return year + "-" + count;
    }

    /**
     * 获得上一次星期
     *
     * @return
     */
    public static String getLastWeek() {
        Date date = new Date();
        date = DateUtil.getAfterDayOrBeforDay(date, -7);
        int count = DateUtil.getYearWeek(DateUtil.formatDate(date, "yyyy-MM-dd"));
        Date weekLastDate = DateUtil.getThisWeekSunday(date);
        String year = DateUtil.formatDate(weekLastDate, "yyyy");
        return year + "-" + count;
    }


    public static String getLastWeek(Date date) {
        date = DateUtil.getAfterDayOrBeforDay(date, -7);
        int count = DateUtil.getYearWeek(DateUtil.formatDate(date, "yyyy-MM-dd"));
        Date weekLastDate = DateUtil.getThisWeekSunday(date);
        String year = DateUtil.formatDate(weekLastDate, "yyyy");
        return year + "-" + count;
    }


    /**
     * 获得下周一
     *
     * @param date
     * @return
     */
    public static Date getNextWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.getThisWeekMonday(date));
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    /**
     * 获得到下周一的秒数
     *
     * @return
     */
    public static Long differNextMondaySeconds() {
        Date today = new Date();
        return Math.abs(DateUtil.compareDateSeconds(today, getNextWeekMonday(today)));
    }


    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }


    public static Date getLastWeekFirstDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return DateUtil.localDateTimeToDate(LocalDateTime.of(DateUtil.dateToLocalDateTime(cal.getTime()).toLocalDate(), LocalTime.MIN));
    }

    public static Date getLastWeekLastDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -1);
        return DateUtil.localDateTimeToDate(LocalDateTime.of(DateUtil.dateToLocalDateTime(cal.getTime()).toLocalDate(), LocalTime.MAX));

    }


}
