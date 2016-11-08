package com.workflow.engine.core.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {
	/** 锁对象 */
    private static final Object lockObj = new Object();

    /** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
    
    public static Date addTime(int timeType,int amount){
    	 Calendar cal = Calendar.getInstance();
    	 cal.add(timeType, amount);
    	return  cal.getTime();
    }
    
    public static String addTime(int timeType,int amount,String pattern){
   	 Calendar cal = Calendar.getInstance();
   	 cal.add(timeType, amount);
   	 return getSdf(pattern).format(cal.getTime());
   }
    
    public static Date setMinute(Date date,int minute){
    	 Calendar cal = Calendar.getInstance();
    	 cal.setTime(date);
    	 cal.set(Calendar.MINUTE, minute);
    	 return cal.getTime();
    }
    
    public static Date setHour(Date date,int hour){
    	Calendar cal = Calendar.getInstance();
	   	 cal.setTime(date);
	   	 cal.set(Calendar.HOUR, hour);
	   	 return cal.getTime();
    }
    public static Date setSecond(Date date,int second){
    	Calendar cal = Calendar.getInstance();
	   	 cal.setTime(date);
	   	 cal.set(Calendar.SECOND, second);
	   	 return cal.getTime();
    }
    
    /**
	 * 增加日期中某类型的某数值。如增加日期
	 * @param date 日期
	 * @param dateType 类型
	 * @param amount 数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}
	
	/**
	 * 增加日期的天数。失败返回null。
	 * @param date 日期
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加天数后的日期
	 */
	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}
	
	/**
	 * 返回字符串
	 * @param dayAmount
	 * @param pattern
	 * @return
	 */
	public static String addDay(int dayAmount,String pattern){
		return format(addDay(new Date(),dayAmount),pattern);
	}

	/**
	 * 返回字符串
	 *
	 * @param amount
	 * @param pattern
	 * @return
	 */
	public static String addYear(int amount,String pattern){
		return format(addInteger(new Date(),Calendar.YEAR,amount),pattern);
	}
	
	public static void main(String[] args){
		System.out.println(DateUtils.addDay(new Date(),1));
		
	}
}
