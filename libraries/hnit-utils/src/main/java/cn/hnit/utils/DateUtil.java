package cn.hnit.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtil {

    public static final String DEFAULT_FMT = "yyyy-MM-dd";

    public static final String SLASH_DATE_FMT = "yyyy/MM/dd";

    public static final String HOUR_FMT = "yyyyMMddHH";


    public static final String DEFAULT_MONTH_FMT = "yyyy-MM";

    public static final String DATE_HOUR_FMT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_MINUTE_FMT = "yyyy-MM-dd HH:mm";

    public static final String DATE_MINUTE_POINT_FMT = "yyyy.MM.dd HH:mm";

    public static final String DATE_POINT_FMT = "yyyy.MM.dd";
    public static final int YEAR = 360;
    public static final int HALF_YEAR = 180;

    public static final long ONE_MINUTE_SECOND = 60L;
    public static final long ONE_HOUR_SECOND = ONE_MINUTE_SECOND * 60;
    public static final long ONE_DAY_SECOND = ONE_HOUR_SECOND * 24;
    public static final long ONE_MONTH_SECOND = ONE_DAY_SECOND * 30;
    public static final long ONE_YEAR_SECOND = ONE_DAY_SECOND * 2365;

    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private final static String[] constellationArr = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
    private final static Long[] constellationArrId = new Long[]{12L, 1L, 2L, 3L, 4L, 5L, 6l, 7L, 8L, 9L, 10L, 11l, 12l};


    /**
     * 获取指定日期的前几月或后几月
     *
     * @param date 指定日期
     * @param num  前2月传 -2 后两月 2
     * @return
     */
    public static Date getAfterMonthOrBeforMonth(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取指定日期的前几天或后几天
     *
     * @param date 指定日期
     * @param num  前2天传 -2 后两天 2
     * @return
     */
    public static Date getAfterDayOrBeforDay(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        date = calendar.getTime();
        return date;
    }


    /**
     * 获取指定日期的后几秒
     *
     * @param date 指定日期
     * @param num  秒数
     * @return 日期对象
     */
    public static String getAfterSeconds(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, num);
        date = calendar.getTime();
        return formatDate(date, DATE_MINUTE_FMT);
    }

    /**
     * 获取指定日期的后几秒
     *
     * @param num 秒数
     * @return 日期对象
     */
    public static String getAfterSeconds(LocalDateTime localDate, int num) {
        Date date = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, num);
        date = calendar.getTime();
        return formatDate(date, DATE_MINUTE_FMT);
    }

    public static LocalDateTime getAfterDayOrBeforDay(LocalDateTime date, int num) {
        return date.plusDays(num);
    }

    /**
     * 获取指定日期的前几天 by ck 2019-5-24
     *
     * @param date 指定日期
     * @param num  1 一天前
     * @return 日期对象
     */
    public static Date getBeforeDay(Date date, int num) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.minusDays(num));
    }

    /**
     * 日期转化 by ck 2019-5-24
     *
     * @param date 日期
     * @return localDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * localDateTime 转 date by ck 2019-5-24
     *
     * @param time localDateTime
     * @return date
     */
    public static Date localDateTimeToDate(LocalDateTime time) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = time.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    /**
     * date to localDate
     *
     * @param date date
     * @return localDate
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 获取今天的开始时间
     *
     * @return
     */
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取今天的结束时间
     *
     * @return
     */
    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 计算时间差
     *
     * @return 两个时间之间的年份
     */
    public static String computeTime(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        LocalDate birDate = LocalDate.parse(dateStr, formatter);
        if (birDate != null) {
            if (now.isBefore(birDate)) {
                return null;
            } else {
                return String.valueOf(birDate.until(now).getYears());
            }
        } else {
            return null;
        }
    }

    /**
     * 时间字符串转换
     *
     * @param dateTime
     * @return
     */
    public static String datetoString(Date dateTime, String model) {
        if (StringUtils.isBlank(model)) {
            model = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dfy = new SimpleDateFormat(model);
        StringBuffer result = new StringBuffer();
        result.append(dfy.format(dateTime));
        return result.toString();

    }


    public static String datetoString(LocalDateTime dateTime, String model) {
        return datetoString(localTimeToDate(dateTime), model);
    }

    public static Date stringToDate(String date, String dateStyle) {
        if (StringUtils.isBlank(dateStyle)) {
            dateStyle = "yyyy-MM-dd HH:mm:ss";
        }
        Date d = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle);
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            log.error("format erro", e);
        }
        return d;
    }

    public static Date addDaysByGetTime(Date dateTime, int n) {
        return new Date(dateTime.getTime() + n * 24 * 60 * 60 * 1000L);
    }

    public static Date addHourByGetTime(Date dateTime, int n) {
        return new Date(dateTime.getTime() + (n) * 60 * 60 * 1000L);
    }

    public static Date addMinuteByGetTime(Date dateTime, int n) {
        return new Date(dateTime.getTime() + (n) * 60 * 1000L);
    }

    /**
     * 指定格式时间转换
     *
     * @param dateStyle 预制要返回的时间   默认 yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Date getSysDate(Date date, String dateStyle) {
        if (StringUtils.isBlank(dateStyle)) {
            dateStyle = "yyyy-MM-dd HH:mm:ss";
        }
        String dateStr = datetoString(date, dateStyle);
        return stringToDate(dateStr, dateStyle);
    }

    /**
     * 指定格式时间转换
     *
     * @param dateStyle 预制要返回的时间   默认 yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static String getSysDateStr(Date date, String dateStyle) {
        if (StringUtils.isBlank(dateStyle)) {
            dateStyle = "yyyy-MM-dd HH:mm:ss";
        }
        String dateStr = datetoString(date, dateStyle);
        return dateStr;
    }

    /**
     * 获取时间差
     * 小于1分钟时---都显示1分钟；
     * <p>
     * 大于1分钟小于等于60分钟---显示具体时间差具体到分钟；
     * <p>
     * 大于1小时小于等于48小时---显示具体时间差，具体到小时；
     * <p>
     * 大于48小时---都显示48小时。
     *
     * @param date      当前时间
     * @param closeTime 关闭时间
     * @return info
     */
    public static String getTimeGap(Date date, Date closeTime) {
        if (closeTime == null) {
            return null;
        }
        LocalDateTime dateTime = null;
        if (date == null) {
            dateTime = LocalDateTime.now();
        } else {
            dateTime = dateToLocalDateTime(date);
        }
        LocalDateTime close = dateToLocalDateTime(closeTime);
        Duration duration = Duration.between(close, dateTime);
        long minutes = duration.toMinutes();
        if (minutes < 1) {
            return "1分钟前";
        } else if (minutes > 1 && minutes <= 60) {
            return minutes + "分钟前";
        }
        long hours = duration.toHours();
        if (hours > 48) {
            return "48小时前";
        } else {
            return hours + "小时前";
        }
    }

    /**
     * 当前时间格式化
     *
     * @param fmt
     * @return
     */
    public static String fmtNow(String fmt) {
        if (fmt == null) fmt = DEFAULT_FMT;
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(fmt));
    }

    /**
     * date 类型转成指定格式 by ck
     *
     * @param date         日期类对象
     * @param formatString 期望格式 eg: yyyy-MM-dd
     * @return 日期字符串 eg: 2019-05-15
     */
    public static String formatDate(Date date, String formatString) {
        if (StringUtils.isBlank(formatString)) {
            formatString = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }


    /**
     * 社区个人主页动态  时间转换
     *
     * @param createTime
     * @return
     */
    public static String formatTime4bbs(Date createTime) {
        if (createTime == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.now();

        LocalDateTime close = dateToLocalDateTime(createTime);

        if (dateTime.getYear() != close.getYear()) {
            return formatDate(createTime, "yyyy-MM-dd");
        } else {
            Duration duration = Duration.between(close, dateTime);
            long minutes = duration.toMinutes();
            if (minutes < 1) {
                return "1分钟前";
            } else if (minutes > 1 && minutes <= 60) {
                return minutes + "分钟前";
            }

            long hours = duration.toHours();

            if (hours > 24) {
                return formatDate(createTime, "MM-dd");
            } else if (hours > 1 && hours < 24) {
                return hours + "小时前";
            }
        }
        return null;
    }


    /**
     * 官方消息 时间转换
     *
     * @param createTime
     * @return
     */
    public static String formatTime4Msg(Date createTime) {
        if (createTime == null) {
            return "";
        }
        Date date = new Date();
        //当天
        String today = cn.hutool.core.date.DateUtil.today();
        //昨天
        String yesterday = cn.hutool.core.date.DateUtil.formatDate(cn.hutool.core.date.DateUtil.offsetDay(date, -1));
        //前天
        String beforeDay = cn.hutool.core.date.DateUtil.formatDate(cn.hutool.core.date.DateUtil.offsetDay(date, -2));

        String tDay = cn.hutool.core.date.DateUtil.formatDate(createTime);

        if (tDay.equals(today)) {
            return cn.hutool.core.date.DateUtil.format(createTime, "HH:mm");
        } else if (tDay.equals(yesterday)) {
            return "昨天 " + cn.hutool.core.date.DateUtil.format(createTime, "HH:mm");
        } else if (tDay.equals(beforeDay)) {
            return "前天 " + cn.hutool.core.date.DateUtil.format(createTime, "HH:mm");
        } else {
            return cn.hutool.core.date.DateUtil.format(createTime, "MM-dd HH:mm");
        }
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
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getThisWeekSunday(Date date) {
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
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day + 6);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 对比两个时间相隔分钟数
     */
    public static Long compareMinutes(Date start, Date end) {
        if (start == null || end == null) {
            return 0L;
        }
        LocalDateTime startDate = dateToLocalDateTime(start);
        LocalDateTime endDate = dateToLocalDateTime(end);
        Duration between = Duration.between(startDate, endDate);
        return between.toMinutes();
    }

    public static Long compareDays(Date start, Date end) {
        if (start == null || end == null) {
            return 0L;
        }
        LocalDateTime startDate = dateToLocalDateTime(start);
        LocalDateTime endDate = dateToLocalDateTime(end);
        Duration between = Duration.between(startDate, endDate);
        return between.toDays();
    }


    /**
     * 对比两个时间相隔分钟数
     */
    public static Long compareDateSeconds(Date start, Date end) {
        if (start == null || end == null) {
            return 0L;
        }
        LocalDateTime startDate = dateToLocalDateTime(start);
        LocalDateTime endDate = dateToLocalDateTime(end);
        Duration between = Duration.between(startDate, endDate);
        return between.toMillis() / 1000;
    }

    /**
     * 对比两个时间相差天数
     *
     * @param start
     * @param end
     * @return
     */
    public static long compareDate(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return 0L;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 对比两个时间相差天数
     *
     * @param start
     * @param end
     * @return
     */
    public static long compareDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0L;
        return ChronoUnit.DAYS.between(start, end);
    }


    /**
     * 对比两个时间相差分钟数
     *
     * @param start
     * @param end
     * @return
     */
    public static long compareMinutes(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0L;
        return ChronoUnit.MINUTES.between(start, end);
    }


    /**
     * 对比两个时间相差秒数
     *
     * @param start
     * @param end
     * @return
     */
    public static long compareSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0L;
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 获取当天剩余的秒数
     *
     * @return
     */
    public static long getCurrentDayLeftSeconds() {
        LocalDateTime time = dateToLocalDateTime(Date.from(LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        return compareSeconds(LocalDateTime.now(), time);
    }

    /**
     * 对比两个时间相隔秒钟
     */
    public static Long compareSecondsDate(Date start, Date end) {
        if (start == null || end == null) {
            return 0L;
        }
        LocalDateTime startDate = dateToLocalDateTime(start);
        LocalDateTime endDate = dateToLocalDateTime(end);
        Duration between = Duration.between(startDate, endDate);
        return between.getSeconds();
    }

    /**
     * 字符串转日期
     *
     * @param dateStr 日期字符
     * @param pattern 格式
     * @return date
     */
    public static Date strToDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date parse = null;
        try {
            parse = sdf.parse(dateStr);
        } catch (Exception e) {
            log.error("strToDate异常:", e);
        }
        return parse;
    }

    /**
     * 对比两个时间相隔分钟数
     *
     * @param type Calendar.DATE  Calendar.MINUTE Calendar.SECOND
     */
    public static Long compareDate(Date start, Date end, int type) {
        if (start == null || end == null) {
            return 0L;
        }
        LocalDateTime startDate = dateToLocalDateTime(start);
        LocalDateTime endDate = dateToLocalDateTime(end);
        Duration between = Duration.between(startDate, endDate);
        if (type == Calendar.DATE) {
            return between.toDays();
        } else if (type == Calendar.MINUTE) {
            return between.toMinutes();
        } else if (type == Calendar.SECOND) {
            return between.getSeconds();
        }
        return 0L;
    }

    /**
     * 获得月初时间
     *
     * @param date
     * @return
     */
    public static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (1 - index));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取上月初时间
     *
     * @param date
     * @return
     */
    public static Date getLastMonthDayOne(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static String format(@NonNull LocalDateTime date, @NonNull String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static String format(@NonNull LocalDateTime date) {
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String format(@NonNull LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_FMT));
    }

    public static String format(@NonNull LocalDate date, @NonNull String fmt) {
        return date.format(DateTimeFormatter.ofPattern(fmt));
    }

    public static Date localDateToMinDate(@NonNull LocalDate localDate) {
        return localDateToDate(localDate, LocalTime.MIDNIGHT);
    }

    public static Date localDateToMaxDate(@NonNull LocalDate localDate) {
        return localDateToDate(localDate, LocalTime.MAX);
    }

    public static Date localDateToDate(@NonNull LocalDate localDate, @NonNull LocalTime localTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atTime(localTime).atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime parseTime(@NonNull String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDate parseDate(@NonNull String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DEFAULT_FMT));
    }

    public static Date parser2Date(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_HOUR_FMT));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static String addDayAndFormat(@NonNull String date, @NonNull int day) {
        return format(parseDate(date).plusDays(day));
    }

    public static Date localTimeToDate(@NonNull LocalDateTime localTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 某时间是否在区间内
     *
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isEffectiveDate(LocalDate nowTime, LocalDate startTime, LocalDate endTime) {
        if (nowTime.isEqual(startTime) || nowTime.isEqual(endTime)) {
            return true;
        }
        return nowTime.isAfter(startTime) && nowTime.isBefore(endTime);
    }

    /**
     * 计算 2 个日期差
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Duration twoDateDifference(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        LocalDateTime startTime = dateToLocalDateTime(startDate);
        LocalDateTime endTime = dateToLocalDateTime(endDate);
        Duration duration = Duration.between(endTime, startTime);
        return duration;
    }

    /**
     * 获得第二天0点的秒数
     *
     * @return
     */
    public static Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 当前日期是当年的第几周
     *
     * @param yyyy_MM_dd
     * @return
     */
    public static int getYearWeek(String yyyy_MM_dd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(yyyy_MM_dd);
        } catch (ParseException e) {
            log.error("error: ", e);
        }
        if (date == null) {
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据生日获得星座
     *
     * @param yyyy_MM_dd
     * @return
     */
    public static String getConstellation(String yyyy_MM_dd) {
        String date = StringUtils.replace(yyyy_MM_dd, "-", "");
        int month = 0;
        int day = 0;
        try {
            Integer dNumber = Integer.parseInt(date);
            day = dNumber % 10 + (dNumber / 10) % 10 * 10;
            dNumber = dNumber / 100;
            month = dNumber % 10 + (dNumber / 10) % 10 * 10;
        } catch (NumberFormatException e) {
            return "";
        }
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    /**
     * 根据生日获得星座Id
     *
     * @param yyyy_MM_dd
     * @return
     */
    public static Long getConstellationId(String yyyy_MM_dd) {
        String date = StringUtils.replace(yyyy_MM_dd, "-", "");
        int month = 0;
        int day = 0;
        try {
            Integer dNumber = Integer.parseInt(date);
            day = dNumber % 10 + (dNumber / 10) % 10 * 10;
            dNumber = dNumber / 100;
            month = dNumber % 10 + (dNumber / 10) % 10 * 10;
        } catch (NumberFormatException e) {
            return 1L;
        }
        return day < dayArr[month - 1] ? constellationArrId[month - 1] : constellationArrId[month];
    }

    /**
     * 根据生日获得星座
     *
     * @param MM_dd
     * @return
     */
    public static String getConstellationByMonthDay(String MM_dd) {
        String[] date = StringUtils.split(MM_dd, "-");
        int month = 0;
        int day = 0;
        try {
            month = Integer.parseInt(date[0]);
            day = Integer.parseInt(date[1]);
        } catch (Exception e) {
            return "";
        }
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    /**
     * 计算连个时间之间的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDayDiffer(Date startDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long startDateTime = 0;
        long endDateTime = 0;
        try {
            startDateTime = dateFormat.parse(dateFormat.format(startDate)).getTime();
            endDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
        } catch (ParseException e) {
            log.error("error: ", e);
        }
        return (int) ((endDateTime - startDateTime) / (1000 * 3600 * 24));
    }


    public static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * 根据生日获取年纪
     *
     * @param birth
     * @return
     */

    public static Integer birthToAge(String birth) {
        try {
            String today = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
            int age = Integer.parseInt(today.substring(0, 4)) - Integer.parseInt(birth.substring(0, 4));
            return age;
        } catch (Exception e) {
            log.error("{}", e.getCause());
        }
        return 18;
    }

    /**
     * 获取某天的上周开始时间
     *
     * @return 日期
     */
    public static LocalDateTime prevWeekBegin(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.plusWeeks(-1).with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atTime(LocalTime.MIN);
    }


    /**
     * 获取某天的本周开始时间
     *
     * @return 日期
     */
    public static LocalDateTime currentWeekBegin(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atTime(LocalTime.MIN);
    }

    public static LocalDateTime yesterdayDayBegin() {
        LocalDate toDay = LocalDate.now();
        LocalDate yesterday = LocalDate.of(toDay.getYear(), toDay.getMonth(), toDay.getDayOfMonth() - 1);
        return LocalDateTime.of(yesterday, LocalTime.MIN);
    }

    public static Date yesterdayEight() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static LocalDateTime yesterdayDayEnd() {
        LocalDate toDay = LocalDate.now();
        LocalDate yesterday = LocalDate.of(toDay.getYear(), toDay.getMonth(), toDay.getDayOfMonth() - 1);
        return LocalDateTime.of(yesterday, LocalTime.MAX);
    }

    /**
     * 获取上周结束时间
     *
     * @return 日期
     */
    public static LocalDateTime prevWeekEnd(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.plusWeeks(-1).with(ChronoField.DAY_OF_WEEK, 7).toLocalDate().atTime(LocalTime.MAX);
    }


    /**
     * 获取本周结束时间
     *
     * @return 日期
     */
    public static LocalDateTime currentWeekEnd(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.with(ChronoField.DAY_OF_WEEK, 7).toLocalDate().atTime(LocalTime.MAX);
    }

    public static void main(String[] args) {
        Date date1 = currentMonthBegin();
        Date date = currentMonthEnd();

        log.info("date1,", date1);
        log.info("date,", date);
    }


    /**
     * 上个月第一天
     *
     * @param date
     * @return
     */
    public static LocalDateTime prevMonthBegin(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.plusMonths(-1).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atTime(LocalTime.MIN);
    }

    /**
     * 上月最后一天
     *
     * @param date
     * @return
     */
    public static LocalDateTime prevMonthEnd(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.plusMonths(-1).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX);
    }


    /**
     * 月第一天
     *
     * @param date
     * @return
     */
    public static LocalDateTime currentMonthBegin(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atTime(LocalTime.MIN);
    }

    /**
     * 月最后一天
     *
     * @param date
     * @return
     */
    public static LocalDateTime currentMonthEnd(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX);
    }

    public static Date currentMonthBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date currentMonthEnd(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }


    public static LocalDateTime timestampToDatetime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }


    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    public static LocalDateTime localDateTimeBegin(LocalDateTime dateTime) {
        return dateTime.withMinute(0).withHour(0).withSecond(0);
    }

    public static LocalDateTime localDateTimeEnd(LocalDateTime dateTime) {
        return dateTime.withMinute(59).withHour(23).withSecond(59);
    }

    public static LocalDateTime localDateTimeBegin(LocalDate dateTime) {
        return LocalDateTime.of(dateTime, LocalTime.MIN);
    }

    public static LocalDateTime localDateTimeEnd(LocalDate dateTime) {
        return LocalDateTime.of(dateTime, LocalTime.MAX);
    }

    /**
     * 获取当前的日期 yyyy-MM-dd
     *
     * @return
     */
    public static String getStrCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    }
}
