package net.monkeystudio.base.utils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by bint on 2017/11/2.
 */
public class TimeUtil {

    public final static  String DEFALUT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static Integer A_DAY_SECONT_COUNT = 24 * 60 * 60;

    /**
     * 得到现在时间的时间戳
     * @return
     */
    public static Long getCurrentTimestamp(){
        Long timestamp = System.currentTimeMillis();

        return timestamp/1000;
    }

    public static long getCurrentTimestamp(Date date){
        Long timestamp = date.getTime();
        return timestamp/1000;
    }

    public static Date getDate(Long timestamp){
        Date date = new Date(timestamp * 1000);
        return date;
    }


    /**
     * 获得当天开始的时间
     * @return
     */
    public static Date getStartTimestamp(){
        java.util.Date date = new java.util.Date();
        return getStartDate(date);
    }

    public static Date getStartDate(Date date){
        String dateStr = dateFormat(date, "yyyy-MM-dd");
        return dateFormat(dateStr, "yyyy-MM-dd");
    }


    public static String dateFormat(java.util.Date date, String sFormat) {

        if (sFormat == null) {
            sFormat = DEFALUT_FORMAT;
        }

        java.text.DateFormat format = new java.text.SimpleDateFormat(sFormat);
        return format.format(date);
    }


    /**
     * 把时间的字段字符串转成时间戳
     * @param dateStr
     * @param strFormat
     * @return 对应的Date类型的实例
     * 例如: 传入 170701和yyMMdd 得到17年7月1号的Date类型
     */
    public static Date dateFormat(String dateStr, String strFormat) {

        if (strFormat == null) {
            return null;
        }
        String year = null;

        Integer yearIndex = strFormat.indexOf("yyyy");
        if(yearIndex != -1 ){
            year = dateStr.substring(yearIndex,yearIndex + 4);
        }else{
            yearIndex = dateStr.indexOf("yy");
            if(yearIndex != -1){
                year = dateStr.substring(yearIndex,yearIndex+2);
            }else{
                java.util.Date date = new java.util.Date();
                java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy");
                year = format.format(date);
            }
        }


        Integer monthIndex = strFormat.indexOf("MM");
        String month = null;
        if(monthIndex != -1){
            month = dateStr.substring(monthIndex,monthIndex+2);
        }else {
            return null;
        }

        Integer dayIndex = strFormat.indexOf("dd");
        String day;
        if(dayIndex != -1){
            day = dateStr.substring(dayIndex , dayIndex+2);
        }else{
            return null;
        }

        Integer hourInteger = strFormat.indexOf("HH");
        String hour = null;
        if(hourInteger != -1){
            hour = dateStr.substring(hourInteger , hourInteger+2);
        }else{
            hour = "00";
        }

        Integer minInteger = strFormat.indexOf("mm");
        String min ;
        if(minInteger != -1){
            min = dateStr.substring(minInteger , minInteger+2);
        }else {
            min = "00";
        }

        Integer secondInteger = strFormat.indexOf("mm");
        String second ;
        if(secondInteger != -1){
            second = dateStr.substring(secondInteger , secondInteger+2);
        }else {
            second = "00";
        }

        String timeStr = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + second;

        Timestamp ts = Timestamp.valueOf(timeStr);

        Long timestamp = ts.getTime();



        Date date = new Date(timestamp);
        return date;

    }
}
