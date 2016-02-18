package com.baoshi.mua.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LazyDateUtil {
	
	public static String getDateDetail(Date date) {
//		Calendar target = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
		
		try {
			Date target = df.parse(df.format(date));
			Date now = df.parse(df.format(new Date(System.currentTimeMillis())));
			long intervalMilli = target.getTime() - now.getTime();
			
			int intervalYear = (int) (intervalMilli / (365 * 24 * 60 * 60 * 1000));
			
			if (intervalYear == 0) {

				df.applyLocalizedPattern("yyyy-MM-dd");

				target = df.parse(df.format(date));
				now = df.parse(df.format(new Date(System.currentTimeMillis())));
				intervalMilli = target.getTime() - now.getTime();

				int intervalDay = (int) (intervalMilli / (24 * 60 * 60 * 1000));

				Calendar cal = Calendar.getInstance(Locale.getDefault());
				cal.setTime(date);

				String dateDetail = getDateDetail(intervalDay, cal);

				df.applyLocalizedPattern("HH:mm");

				String time = df.format(date);

				return new StringBuffer().append(dateDetail).append(" ").append(time).toString();

			}else if(intervalYear == -1){
				df.applyLocalizedPattern("MM-dd HH:mm");
				return df.format(date);
			}else{
				df.applyLocalizedPattern("yy-MM-dd HH:mm");
				return df.format(date);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		

	}

	private static String getDateDetail(int xcts, Calendar target) {
		switch (xcts) {
		case 0:
			return Constants.TODAY;
		case 1:
			return Constants.TOMORROW;
		case 2:
			return Constants.AFTER_TOMORROW;
		case -1:
			return Constants.YESTERDAY;
		case -2:
			return Constants.BEFORE_YESTERDAY;
		default:
			int dayForWeek = 0;
			dayForWeek = target.get(Calendar.DAY_OF_WEEK);
			switch (dayForWeek) {
			case 1:
				return Constants.SUNDAY;
			case 2:
				return Constants.MONDAY;
			case 3:
				return Constants.TUESDAY;
			case 4:
				return Constants.WEDNESDAY;
			case 5:
				return Constants.THURSDAY;
			case 6:
				return Constants.FRIDAY;
			case 7:
				return Constants.SATURDAY;
			default:
				return null;
			}

		}
	}

	private class Constants {
		public static final String TODAY = "今天";
		public static final String YESTERDAY = "昨天";
		public static final String TOMORROW = "明天";
		public static final String BEFORE_YESTERDAY = "前天";
		public static final String AFTER_TOMORROW = "后天";
		public static final String SUNDAY = "星期日";
		public static final String MONDAY = "星期一";
		public static final String TUESDAY = "星期二";
		public static final String WEDNESDAY = "星期三";
		public static final String THURSDAY = "星期四";
		public static final String FRIDAY = "星期五";
		public static final String SATURDAY = "星期六";
	}
}
