package net.monkeystudio.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * @author hebo2
 */
public class CommonUtils 
{
	private static final String regExHtml = "<[^>]+>"; // HTML标签的正则表达式
	private static InputStream in = null; //读取properties文件
	
	/**
	 * String分割为List<String>
	 * @param str
	 * @param split
	 * @return
	 */
	public static List<String> splitString2List(String str, String split)
	{
		List<String> strList = new Vector<String>();
		
		if ( str == null )
		{
			return strList;
		}
		
		String[] strTmp;
		
		if ( split !=  null)
		{
			strTmp = str.split(split);
		}
		else
		{
			strTmp = str.split(",");
		}
		
		for (String s : strTmp)
		{
			strList.add(s);
		}
		
		return strList;
	}
	
	/**
	 * 获取HttpServletRequest请求报文的body字段(编码类型只支持UTF-8)
	 * @param request
	 * @return String httpBody
	 */
	public final static String getHttpBodyFromRequest(HttpServletRequest request)
	{

		String httpBody = "";
		
		try
		{
			 InputStreamReader reader = new InputStreamReader(request.getInputStream(), "UTF-8"); 
			 char[] buff = new char[1024]; 
			 int length = 0; 
		 		 
			 while ((length = reader.read(buff)) != -1) 
			 { 
				 	String x = new String(buff, 0, length); 
				 	httpBody += x;
			 } 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace(); 
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		return httpBody;
	}
	
	/**
	 * list转为字符串
	 * @param <T>
	 * @param list  
	 * @param split 分隔符，自动去前后空格，如果为空默认以逗号分隔
	 * @return
	 */
	public static <T> String List2String(List<T> list, String split)
	{
		String retStr = "";
		String sp = ",";  //默认分隔符
		
		if ( list == null || list.size()==0 )
		{
			return null;
		}
		
		if ( split != null && ! "".equals( split.trim()) )
		{
			sp = split.trim();
		}
			
		Iterator<T> it = list.iterator();
		while(it.hasNext())
		{
			retStr += it.next().toString();
			if (it.hasNext())
			{
				retStr += sp;
			}
		}
		
		return retStr;
	}
	
	/**
	 * 取len位随机数
	 * @param len 支持1-10位，小于1或大于10返回null
	 * @return 
	 */
	public static Long getRandom(int len)
	{
		if(len < 1 || len > 10)
		{
			return null;
		}
		
		long base = 1;
		for ( int i = 0; i < len; i ++ )
		{
			base *= 10;
		}
		
		long random = Math.round(Math.random()*base);
		if ( len == 1)
		{
			return random;
		}
		
		while(random < base/10 || random > base-1) //保险，小概率
		{
			random = Math.round(Math.random()*base);
		}
		
		return random;
	}
	
	/**判断字符是否为数字
	 * @param s 要判断的字符串
	 * @return
	 **/
	public final static boolean isDigist(String s){
		if(s==null || s.length()<=0 || s.equals("") ){
			return false;
		}
		char[] c = s.toCharArray();
		for(int i = 0;i<c.length;i++){
			if(!Character.isDigit(c[i])){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 格式化时间。
	 * 距当前时间1小时间，返回 x分钟前
	 * 距当前时间1小时以上，24小时内，返回 x小时前
	 * 距当前时间1天以上，显示 X月X日
	 * @param timeSec 秒数
	 * @returns {*}
	 */
	public final static String formatPubTime(long timeSec)
	{
	    long curTime =  System.currentTimeMillis()/1000;
	    long tmp = curTime - timeSec;

	    if (tmp < 3600)
	    {
	        return (long)Math.floor(tmp/60) + "分钟前";
	    }

	    if (tmp < 24*3600)
	    {
	        return (long)Math.floor(tmp/3600) + "小时前";
	    }

	    Date date = new Date(timeSec*1000);
	    
	    if (date.getYear() == new Date().getYear())
	    {
	        return formatTimeSecs(timeSec, "M月d日");
	    }

	    return formatTimeSecs(timeSec,"yyyy年M月d日");
	}
	
	/**
	 * 格式化时间2。
	 * 距当前时间1小时间，返回 x分钟前
	 * 距当前时间1小时以上，24小时内，返回 x小时前
	 * 距当前时间1天以上，显示 X月X日
	 * @param timeSec 秒数
	 * @returns {*}
	 */
	public final static String formatPubTime2(long timeSec)
	{
	    long curTime =  System.currentTimeMillis()/1000;
	    long tmp = curTime - timeSec;

	    if (tmp < 3600)
	    {
	        return (long)Math.floor(tmp/60) + "分钟前";
	    }

	    if (tmp < 24*3600)
	    {
	        Long h = (long)Math.floor(tmp/3600) ;
	        
	        tmp = h - (tmp%3600);
	        
	        Long m = (long)Math.floor(tmp/60);
	        
	        return h + "小时" + m + "分钟前";
	    }
	    
	    if ( tmp < 7*24*3600 ){
	    	Long d = (long)Math.floor(tmp/(3600*24));
	    	
	    	tmp =  tmp - (d*24*3600);
	    	Long h = (long)Math.floor(tmp/3600);
	    	
	    	tmp = tmp - (h*3600);
	    	Long m = (long)Math.floor(tmp/60);
	    	
	    	return d + "天" + h + "小时" + m + "分钟前"; 
	    }

	    Date date = new Date(timeSec*1000);
	    
	    if (date.getYear() == new Date().getYear())
	    {
	        return formatTimeSecs(timeSec, "M月d日HH时mm分");
	    }

	    return formatTimeSecs(timeSec,"yyyy年M月d日HH时mm分");
	}
	
	/**
	 * 格式化时间秒
	 * @param secs 秒数
	 * @return
	 */
	public final static String formatTimeSecs(long secs, String format)
	{
		Date d = timeSecs2Date(secs);
		
		return dateFormat(d, format);
	}
	
	/**
	 * 秒转Date对像
	 * @param timeSecs
	 * @return
	 */
	public static Date timeSecs2Date(long timeSecs)
	{
		return new Date(timeSecs * 1000);
	}
	
	
	/**
	 * 日期字符串转为Date对象
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date string2Date(String dateStr, String format){
		
		if ( format == null ){
			format = "yyyy-MM-dd";
		}
 
		try  
		{  
		    SimpleDateFormat sdf = new SimpleDateFormat(format);  
		    Date date = sdf.parse(dateStr);  
		    return date;
		}  
		catch (ParseException e)  
		{  
		    Log.w(e.getMessage());
		    return null;
		} 
	}
	
	
	/**
	 * 日期格式化
	 * @param format (yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static String dateFormat(Date date, String sFormat)
	{
		if ( sFormat == null)
		{
			sFormat = "yyyy-MM-dd HH:mm:ss";
		}
		
		java.text.DateFormat format = new SimpleDateFormat(sFormat);
        return format.format(date);
	}
	
	/**
	 * 取文件后缀名
	 * @param filename
	 * @return
	 */
	public static String getFilePostfix(String filename)
	{
		int index =  filename.lastIndexOf(".");
		if ( index < 0 )
		{
			return "";
		}
		
		return filename.substring(index + 1);
	}
	
	/**
	 * 过滤html标签
	 * @param html string
	 * @return plain string
	 */
	public static String delHtmlTag(String html)
	{
		 Pattern p_html = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);  
	     Matcher m_html = p_html.matcher(html);  
	     return  m_html.replaceAll(""); // 过滤html标签  
	}
	

	/**获取config.properties配置文件对应值
	 * @param key
	 * @return
	 */
	public static String getPropertiesValue(String key){
		in = CommonUtils.class.getResourceAsStream("/resources/spring-mvc/config.properties");
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p.getProperty(key);
	}
	
	/**获取properties配置文件对应值
	 * @param filePath  配置文件地址
	 * @param key   键
	 * @return
	 */
	public static String getPropertiesFileValue(String filePath,String key){
		in = CommonUtils.class.getResourceAsStream(filePath);
		Properties p = new Properties();
		try {
			p.load(in);
			return p.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 
	//邮箱验证
	public static boolean checkEmail(String email) {  
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";  
        return Pattern.matches(regex, email);  
    }  
	
	/**
	 * 取文件后缀名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFilenamePostfix(String filename) {
		if (StringUtils.isBlank(filename)) {
			return "";
		}

		int index = filename.lastIndexOf(".");
		if (index < 0) {
			return "";
		}
		return filename.substring(index + 1);
	}
	
	
	/**
	 * 生成一个新订单号
	 * @return
	 */
	public static String getNewOrderId()
	{
		String orderId = dateFormat(new Date(), "yyyyMMddHHmmss");
		orderId += getRandom(3);
		
		return orderId;
	}
	
	public static int compareVer(String v1, String v2)
	{
		String[] v1L = v1.split("\\.");
		String[] v2L = v2.split("\\.");
		
		//minSize为较短的列表长度
		int minSize = v1L.length;
		if ( v1L.length > v2L.length )
		{
			minSize = v2L.length;
		}
		
		for(int i = 0; i < minSize; i++)
		{
			if(Integer.parseInt(v1L[i]) != Integer.parseInt(v2L[i]))
			{
				return Integer.parseInt(v1L[i]) - Integer.parseInt(v2L[i]);
			}
		}
		
		return v1L.length - v2L.length;
	}
	
	public static Integer char2Ascii(char c){
		
		return Integer.parseInt("" + c);
	}
	
	public static Integer page2startIndex(Integer page, Integer pageSize){
		
		if ( page < 1 ){
			return 0;
		}
		
		return pageSize * ( page - 1 );
	}
	
	/**
	 * 检查手机号格式是否正确
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone){
		
		if ( phone == null ){
			return false;
		}
		
		if ( phone.length() != 11 ){
			return false;
		}
		
		Pattern p = Pattern.compile("^1\\d{10}$"); 
		Matcher m = p.matcher(phone);  
		return m.matches();
	}
	
	public static String base64encode(String str){
		
		byte[] bytes = str.getBytes();
		
		byte[] b64 = Base64.getEncoder().encode(bytes);
		
		return new String(b64);
	}
	
	public static String base64decode(String str){
		
		byte[] b64 = str.getBytes();
		
		byte[] bytes = Base64.getDecoder().decode(b64);
		
		return new String(bytes);
		
	}
	
	/**
	 * 字符串s如果不够length指定长度，在前面补0
	 * @param s
	 * @param length
	 * @return
	 */
	public static String zeroFillLeft(String s, Integer length){
		
		s = s.trim();
		
		if ( s == null || length == null || length < 1 || s.length() >= length ){
			return s;
		}
		
		int fillNum = length - s.length();
		for ( int i = 0; i < fillNum; i++ ){
			s = "0" + s;
		}
		
		return s;
	}
	
	/**
	 * 通过Date获取当天星期几
	 * @param date
	 * @return [0, 1, 2, 3, 4, 5, 6] 对应 [周日,周一,周二,周三,周四,周五,周六]
	 */
	public static int weekFromDate(Date date){
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int w = calendar.get(Calendar.DAY_OF_WEEK);
		
		return w - 1;
	}
	
	/**
	 * 通过Date获取当天星期几
	 * @param date
	 * @return
	 */
	public static String weekdayFromDate(Date date){
		
		String[] weekdays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
		
		int i = weekFromDate(date);
		
		return weekdays[i];
	}
	
	/**
	 * 时间推移days天，days为正往后推，days为负往前推
	 * @param date
	 * @param days 为正，往后推。
	 * @return
	 */
	public static Date dateOffset(Date date ,int days){
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		
		Date mewDate = calendar.getTime();
		
		return mewDate;
	}
	
	/**
	 * 时间推移hours小时，hours为正往后推，hours为负往前推
	 * @param date
	 * @param hours 为正，往后推。
	 * @return
	 */
	public static Date hourOffset(Date date, int hours){
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, hours);
		
		Date mewDate = calendar.getTime();
		
		return mewDate;
	}
	
	/**
	 * 取date当周的开始时间
	 * @param date yyyy-MM-dd 00:00:00
	 * @return
	 */
	public static Date weekStartTime(Date date){
		
		int w = weekFromDate(date);
		
		Date startDate = dateOffset(date, -w);
		
		return dateFloor(startDate);
		
	}
	
	/**
	 * 取date当周结束时间
	 * @param date
	 * @return
	 */
	public static Date weekEndTime(Date date){
		
		int w = weekFromDate(date);
		
		int offset = 6 - w;
		Date endDate = dateOffset(date, offset);
		
		return dateCeil(endDate);
	}
	
	/**
	 * 当天开始时间
	 * @param date
	 * @return
	 */
	public static Date dateStartTime(Date date){
		
		String dateStr = CommonUtils.dateFormat(date, "yyyy-MM-dd");
		
		Date dateStartTime = CommonUtils.string2Date(dateStr + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		
		return dateStartTime;
	}
	
	/**
	 * 当天结束时间
	 * @param date
	 * @return
	 */
	public static Date dateEndTime(Date date){
		
		String dateStr = CommonUtils.dateFormat(date, "yyyy-MM-dd");
		
		Date dateEndTime = CommonUtils.string2Date(dateStr + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		return dateEndTime;
	}
	
	/**
	 * 日期取整 yyyy-MM-dd HH:mm:ss 转为 yyyy-MM-dd 00:00:00
	 * @param date
	 * @return
	 */
	public static Date dateFloor(Date date){
		
		String dateFormat = dateFormat(date, "yyyy-MM-dd");
		
		Date newDate = string2Date(dateFormat, "yyyy-MM-dd");
		
		return newDate;
	}
	
	/**
	 * 日期ceil，yyyy-MM-dd HH:mm:ss 转为 yyyy-MM-dd 23:59:59
	 * @param date
	 * @return
	 */
	public static Date dateCeil(Date date){

		String dateFormat = dateFormat(date, "yyyy-MM-dd 23:59:59");
		
		Date newDate = string2Date(dateFormat, "yyyy-MM-dd HH:mm:ss");
		
		return newDate;
	}
	
	/**
    * 生成随机字符串
    * @param base 基础字符集
    * @param length 生成字符串长度
    * @return
    */
   public static String getRandomString(String base, int length) { 

       Random random = new Random();   
       StringBuffer sb = new StringBuffer();   
       for (int i = 0; i < length; i++) {   
           int number = random.nextInt(base.length());   
           sb.append(base.charAt(number));   
       }
       
       return sb.toString();   
    }  
   
   /**
    * 获取UUID
    * @return
    */
   public static String randomUUID(){
	   
	   return UUID.randomUUID().toString().replaceAll("-", "");
   }

}
