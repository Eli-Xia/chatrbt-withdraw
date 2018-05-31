package net.monkeystudio.base.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 * @date 2018/1/17 17:32
 * 时间处理类
 */
public class DateUtils {

    public final static Long DAY_MILLISECOND = 24 * 60 * 60 * 1000L;

    /**
     * 判断该Date是否为昨天或昨天以前  小于今天0点
     *
     * @param date
     * @return
     */
    public static boolean isAtLeastYesterday(Date date) {
        return getBeginDate(new Date()).compareTo(date) > 0 ? true : false;
    }

    //从昨天开始往前推算7天,组装报表时间默认数据
    public static List<Date> getChartDefaultDates() {
        List<Date> dates = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Date today = new Date();
            long time = today.getTime();
            long internalTime = i * 24 * 3600 * 1000;
            long chartTime = time - internalTime;
            Date d = new Date(chartTime);
            dates.add(d);
        }
        return dates;
    }

    /**
     * 默认的结束时间  当天最后一秒
     *
     * @return 昨天
     */
    public static Date getDefaultEndDate() {
        long today = new Date().getTime();
        long internalTime = 24 * 3600 * 1000;
        Date date = new Date(today - internalTime);
        return getEndDate(date);
    }

    /**
     * 默认的开始时间  当天0点
     *
     * @return 昨天往前推6天
     */
    public static Date getDefaultBeginDate() {
        long today = new Date().getTime();
        long internalTime = 24 * 3600 * 1000 * 7;
        Date date = new Date(today - internalTime);
        return getBeginDate(date);
    }


    //获取两个时间的间隔,返回的是时间毫秒值
    public static long getBetweenTwoDate(Date one, Date other) {
        return Math.abs(one.getTime() - other.getTime());
    }

    //获取当前时间距离第二天0点的秒数
    public static int getCacheSeconds(){
        Date current = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        //设置时分秒为0
        c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH)+1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date nextDay = c.getTime();
        Long sec = (nextDay.getTime()-current.getTime()) / 1000 ;
        return sec.intValue();
    }


    //获取开始时间
    public static Date getBeginDate(Date current) {
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        //设置时分秒为0
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    //获取结束时间
    public static Date getEndDate(Date current) {
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        //设置时分秒为0
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        //把天数加1,秒数减1
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        return c.getTime();
    }

    /**
     * 获取指定日期指定天数前的时间
     *
     * @param date     时期
     * @param dayCount 天数 ,例如一天前 ,就是1
     * @return
     */
    public static Date getDay(Date date, Integer dayCount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.DATE, dayCount);
        Date result = cal.getTime();

        return result;
    }


    public static Date getYesterday(Date date) {
        return getDay(date, -1);
    }


    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算相隔之间的天数 (包括开始开始那天和结束那天)
     *
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static int getDiscrepantDays(Date dateStart, Date dateEnd) {
        dateStart = getBeginDate(dateStart);
        dateEnd = getEndDate(dateEnd);
        return (int) ((dateEnd.getTime() - dateStart.getTime()) / DAY_MILLISECOND) + 1;
    }


}
