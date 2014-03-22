package com.tzc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class DateUtils {
	private static Logger logger = LogUtil.getLogger();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat DAILY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

	public static Date getLastDay(int day, Date fromDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, -day);
		return c.getTime();
	}

	public static Date string2Date(String dateStr) {
		if (dateStr != null) {
			try {
				return DATE_FORMAT.parse(dateStr);
			} catch (ParseException e) {
				logger.error("#日期转换异常");
			}
		}
		return null;
	}

	public static String date2String(Date date) {
		if (date != null) {
			return DATE_FORMAT.format(date);
		} else {
			logger.error("#日期转换异常");
			return null;
		}
	}
	public static String date2StringNew(String dateStr){
		Date resDate = null;
		if (dateStr.matches("\\w+ \\w+ \\d+ \\d+:\\d+:\\d+ CST \\d+")) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
			try {
				resDate =  dateFormat.parse(dateStr);
			} catch (ParseException e) {
				logger.error("#日期转换异常");
			}
		}
		String res = date2String(resDate);
		return res;
	}
	
	public static String date2DailyString(Date date) {
		if (date != null) {
			return DAILY_DATE_FORMAT.format(date);
		} else {
			logger.error("#日期转换异常");
			return null;
		}
	}


	public static Date weiboDateStr2date(String dateStr) {
		if (dateStr.matches("\\w+ \\w+ \\d+ \\d+:\\d+:\\d+ \\+0800 \\d+")) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
			try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				logger.error("#日期转换异常");
			}
		}
		return null;
	}

	public static String formatWeiboDate(String dateStr) {
		if (dateStr.matches("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+")) {
			return dateStr;
		} else {
			Date date = weiboDateStr2date(dateStr);
			if (date == null) {
				return null;
			} else {
				return DATE_FORMAT.format(date);
			}
		}
	}
	
	public static void main(String[] args) {
	}
	
}
