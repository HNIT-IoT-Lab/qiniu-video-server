package cn.hnit.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类.
 * 
 * 在不方便使用joda-time时，使用本类降低Date处理的复杂度与性能消耗, 封装Common Lang及移植Jodd的最常用日期方法
 * 
 * @author calvin
 */
@Slf4j
public class DateCommonUtils {

	public static final long MILLIS_PER_SECOND = 1000; // Number of milliseconds in a standard second.

	public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND; // Number of milliseconds in a standard minute.

	public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE; // Number of milliseconds in a standard hour.

	public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR; // Number of milliseconds in a standard day.

	private static final int[] MONTH_LENGTH = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/**
	 * 是否同一天.
	 * 
	 * @see DateUtils#isSameDay(Date, Date)
	 */
	public static boolean isSameDay(final Date date1, final Date date2) {
		return DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 是否同一时刻.
	 */
	public static boolean isSameTime(final Date date1, final Date date2) {
		// date.getMillisOf() 比date.getTime()快
		return date1.compareTo(date2) == 0;
	}

	/**
	 * 判断日期是否在范围内，包含相等的日期
	 */
	public static boolean isBetween(final Date date, final Date start, final Date end) {
		if (date == null || start == null || end == null || start.after(end)) {
			throw new IllegalArgumentException("some date parameters is null or dateBein after dateEnd");
		}
		return !date.before(start) && !date.after(end);
	}

	//////////// 往前往后滚动时间//////////////

	/**
	 * 加一月
	 */
	public static Date addMonths(final Date date, int amount) {
		return DateUtils.addMonths(date, amount);
	}

	/**
	 * 减一月
	 */
	public static Date subMonths(final Date date, int amount) {
		return DateUtils.addMonths(date, -amount);
	}

	/**
	 * 加一周
	 */
	public static Date addWeeks(final Date date, int amount) {
		return DateUtils.addWeeks(date, amount);
	}

	/**
	 * 减一周
	 */
	public static Date subWeeks(final Date date, int amount) {
		return DateUtils.addWeeks(date, -amount);
	}

	/**
	 * 加一天
	 */
	public static Date addDays(final Date date, final int amount) {
		return DateUtils.addDays(date, amount);
	}

	/**
	 * 减一天
	 */
	public static Date subDays(final Date date, int amount) {
		return DateUtils.addDays(date, -amount);
	}

	/**
	 * 加一小时
	 */
	public static Date addHours(final Date date, int amount) {
		return DateUtils.addHours(date, amount);
	}

	/**
	 * 减一小时
	 */
	public static Date subHours(final Date date, int amount) {
		return DateUtils.addHours(date, -amount);
	}

	/**
	 * 加一分钟
	 */
	public static Date addMinutes(final Date date, int amount) {
		return DateUtils.addMinutes(date, amount);
	}

	/**
	 * 减一分钟
	 */
	public static Date subMinutes(final Date date, int amount) {
		return DateUtils.addMinutes(date, -amount);
	}

	/**
	 * 终于到了，续一秒.
	 */
	public static Date addSeconds(final Date date, int amount) {
		return DateUtils.addSeconds(date, amount);
	}

	/**
	 * 减一秒.
	 */
	public static Date subSeconds(final Date date, int amount) {
		return DateUtils.addSeconds(date, -amount);
	}

	//////////// 直接设置时间//////////////

	/**
	 * 设置年份, 公元纪年.
	 */
	public static Date setYears(final Date date, int amount) {
		return DateUtils.setYears(date, amount);
	}

	/**
	 * 设置月份, 0-11.
	 */
	public static Date setMonths(final Date date, int amount) {
		return DateUtils.setMonths(date, amount);
	}

	/**
	 * 设置日期, 1-31.
	 */
	public static Date setDays(final Date date, int amount) {
		return DateUtils.setDays(date, amount);
	}

	/**
	 * 设置小时, 0-23.
	 */
	public static Date setHours(final Date date, int amount) {
		return DateUtils.setHours(date, amount);
	}

	/**
	 * 设置分钟, 0-59.
	 */
	public static Date setMinutes(final Date date, int amount) {
		return DateUtils.setMinutes(date, amount);
	}

	/**
	 * 设置秒, 0-59.
	 */
	public static Date setSeconds(final Date date, int amount) {
		return DateUtils.setSeconds(date, amount);
	}

	/**
	 * 设置毫秒.
	 */
	public static Date setMilliseconds(final Date date, int amount) {
		return DateUtils.setMilliseconds(date, amount);
	}

	///// 获取日期的位置//////
	/**
	 * 获得日期是一周的第几天. 已改为中国习惯，1 是Monday，而不是Sundays.
	 */
	public static int getDayOfWeek(final Date date) {
		int result = get(date, Calendar.DAY_OF_WEEK);
		return result == 1 ? 7 : result - 1;
	}

	/**
	 * 日期转化成星期
	 * @param datetime
	 * @return
	 */
	public static String dateToWeek(String datetime) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		Date date;
		try {
			date = f.parse(datetime);
			cal.setTime(date);
		} catch (ParseException e) {
			log.error("error: ", e);
		}
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		return weekDays[w];
	}

	/**
	 * 获得日期是一年的第几天，返回值从1开始
	 */
	public static int getDayOfYear(final Date date) {
		return get(date, Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获得日期是一月的第几周，返回值从1开始.
	 * 
	 * 开始的一周，只要有一天在那个月里都算. 已改为中国习惯，1 是Monday，而不是Sunday
	 */
	public static int getWeekOfMonth(final Date date) {
		return getWithMondayFirst(date, Calendar.WEEK_OF_MONTH);
	}

	/**
	 * 获得日期是一年的第几周，返回值从1开始.
	 * 
	 * 开始的一周，只要有一天在那一年里都算.已改为中国习惯，1 是Monday，而不是Sunday
	 */
	public static int getWeekOfYear(final Date date) {
		return getWithMondayFirst(date, Calendar.WEEK_OF_YEAR);
	}

	private static int get(final Date date, int field) {
		Validate.notNull(date, "The date must not be null");
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(date);

		return cal.get(field);
	}

	private static int getWithMondayFirst(final Date date, int field) {
		Validate.notNull(date, "The date must not be null");
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(date);
		return cal.get(field);
	}

	///// 获得往前往后的日期//////

	/**
	 * 2016-11-10 07:33:23, 则返回2016-1-1 00:00:00
	 */
	public static Date beginOfYear(final Date date) {
		return DateUtils.truncate(date, Calendar.YEAR);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-12-31 23:59:59.999
	 */
	public static Date endOfYear(final Date date) {
		return new Date(nextYear(date).getTime() - 1);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2017-1-1 00:00:00
	 */
	public static Date nextYear(final Date date) {
		return DateUtils.ceiling(date, Calendar.YEAR);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-1 00:00:00
	 */
	public static Date beginOfMonth(final Date date) {
		return DateUtils.truncate(date, Calendar.MONTH);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-30 23:59:59.999
	 */
	public static Date endOfMonth(final Date date) {
		return new Date(nextMonth(date).getTime() - 1);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-12-1 00:00:00
	 */
	public static Date nextMonth(final Date date) {
		return DateUtils.ceiling(date, Calendar.MONTH);
	}

	/**
	 * 2017-1-20 07:33:23, 则返回2017-1-16 00:00:00
	 */
	public static Date beginOfWeek(final Date date) {
		return DateUtils.truncate(DateCommonUtils.subDays(date, DateCommonUtils.getDayOfWeek(date) - 1), Calendar.DATE);
	}

	/**
	 * 2017-1-20 07:33:23, 则返回2017-1-22 23:59:59.999
	 */
	public static Date endOfWeek(final Date date) {
		return new Date(nextWeek(date).getTime() - 1);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-22 00:00:00
	 */
	public static Date nextWeek(final Date date) {
		return DateUtils.truncate(DateCommonUtils.addDays(date, 8 - DateCommonUtils.getDayOfWeek(date)), Calendar.DATE);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-10 00:00:00
	 */
	public static Date beginOfDate(final Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-23 23:59:59.999
	 */
	public static Date endOfDate(final Date date) {
		return new Date(nextDate(date).getTime() - 1);
	}

	/**
	 * 2016-11-10 07:33:23, 则返回2016-11-11 00:00:00
	 */
	public static Date nextDate(final Date date) {
		return DateUtils.ceiling(date, Calendar.DATE);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 07:00:00
	 */
	public static Date beginOfHour(final Date date) {
		return DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-23 07:59:59.999
	 */
	public static Date endOfHour(final Date date) {
		return new Date(nextHour(date).getTime() - 1);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 08:00:00
	 */
	public static Date nextHour(final Date date) {
		return DateUtils.ceiling(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 07:33:00
	 */
	public static Date beginOfMinute(final Date date) {
		return DateUtils.truncate(date, Calendar.MINUTE);
	}

	/**
	 * 2017-1-23 07:33:23, 则返回2017-1-23 07:33:59.999
	 */
	public static Date endOfMinute(final Date date) {
		return new Date(nextMinute(date).getTime() - 1);
	}

	/**
	 * 2016-12-10 07:33:23, 则返回2016-12-10 07:34:00
	 */
	public static Date nextMinute(final Date date) {
		return DateUtils.ceiling(date, Calendar.MINUTE);
	}

	////// 闰年及每月天数///////
	/**
	 * 是否闰年.
	 */
	public static boolean isLeapYear(final Date date) {
		return isLeapYear(get(date, Calendar.YEAR));
	}

	/**
	 * 是否闰年，移植Jodd Core的TimeUtil
	 * 
	 * 参数是公元计数, 如2016
	 */
	public static boolean isLeapYear(int y) {
		boolean result = ((y % 4) == 0) && // must be divisible by 4...
                ((y < 1582) || // and either before reform year...
                        ((y % 100) != 0) || // or not a century...
                        ((y % 400) == 0));

        // or a multiple of 400...
        // for leap year.
        return result;
	}

	/**
	 * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
	 */
	public static int getMonthLength(final Date date) {
		int year = get(date, Calendar.YEAR);
		int month = get(date, Calendar.MONTH);
		return getMonthLength(year, month);
	}

	/**
	 * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
	 */
	public static int getMonthLength(int year, int month) {

		if ((month < 1) || (month > 12)) {
			throw new IllegalArgumentException("Invalid month: " + month);
		}
		if (month == 2) {
			return isLeapYear(year) ? 29 : 28;
		}

		return MONTH_LENGTH[month];
	}

	/**
	 * 获取某段时这里写代码片间内的所有日期
	 * @param dBegin
	 * @param dEnd
	 * @return
	 */
	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List<Date> lDate = new ArrayList<Date>();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime()))  {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}

	/**
	 * 获取某段时这里写代码片间内的所有日期字符串
	 * @param dBegin
	 * @param dEnd
	 * @return
	 */
	public static List<String> findDatesString(Date dBegin, Date dEnd) {
		List<String> dateString = new ArrayList<>(16);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		dateString.add(format.format(dBegin));
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime()))  {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			dateString.add(format.format(calBegin.getTime()));
		}
		return dateString;
	}
}
