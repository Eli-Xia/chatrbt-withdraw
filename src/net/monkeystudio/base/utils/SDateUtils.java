package net.monkeystudio.base.utils;

import java.text.ParseException;
import java.util.Date;

public class SDateUtils {

	/**
	 * 字符串本地时间解析成Date对象
	 * @param locTimeStr
	 * @return
	 */
	public static Date parseDate(String locTimeStr){
		
		if ( locTimeStr == null ){
			return null;
		}
		
		String[] patterns = new String[]{
				"yyyy年MM月",
				"yyyy年MM月dd日",
				"yyyy年MM月dd日 HH时mm分ss秒",
				"yyyy-MM",
				"yyyyMM",
				"yyyy/MM",   
                "yyyyMMdd",
                "yyyy-MM-dd",
                "yyyy/MM/dd",   
                "yyyyMMddHHmmss",   
                "yyyy-MM-dd HH:mm:ss",   
                "yyyy/MM/dd HH:mm:ss"};   
		try {
			return org.apache.commons.lang.time.DateUtils.parseDate(locTimeStr, patterns);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
