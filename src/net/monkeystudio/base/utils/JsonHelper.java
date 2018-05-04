package net.monkeystudio.base.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONException;

/**
 * json解析工具
 * @author hebo2
 *
 */
public class JsonHelper 
{
	/**
	 * Json字符串转换成List
	 * @param List<T>
	 * @param json  Json字符串
	 * @param cls   Bean类
	 * @return
	 */
	public static <T> List<T> fromJsonArray(String json, Class cls) throws JSONException
	{
		JSONTokener parser = new JSONTokener(json);
		JSONArray obj = (JSONArray)parser.nextValue();
		
		return JSONArray.toList(obj, cls);
	}
	
	/**
	 * Json字符串转换成Bean
	 * @param <T>
	 * @param json  Json字符串
	 * @param cls   Bean类
	 * @return
	 */
	public static <T> T fromJson(String json, Class cls) throws JSONException
	{
		JSONTokener parser = new JSONTokener(json);
		JSONObject obj = (JSONObject)parser.nextValue();
		
		T t = (T)obj.toBean(obj, cls);
		return t;
	}
	
	/**
	 * Json字符串转成Map
	 * @param json Json字符串
	 * @return
	 */
	public static Map json2Map(String json) throws JSONException
	{
		HashMap map = JsonHelper.fromJson(json, HashMap.class);
		return map;
	}
	
	/**
	 * 对象转json字符串
	 * @param <T>
	 * @param bean 数据类
	 * @return
	 */
	public static <T> String bean2json(T bean) throws JSONException
	{
		JsonConfig jsonConfig = new JsonConfig();  
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateProcessor());
		
		return JSONObject.fromObject(bean, jsonConfig).toString();
	}
	
	/**
	 * 对象转json字符串
	 * @param <T>
	 * @param bean 数据类
	 * @param excludes 排除字段
	 * @return
	 */
	public static <T> String bean2json(T bean, String[] excludes) throws JSONException
	{
		JsonConfig config = new JsonConfig();
		config.setExcludes(excludes);
		return JSONObject.fromObject(bean,config).toString();
	}
	
	/**
	 * 跟据key值从json字符串中获取string型value值
	 * @param json
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public static String getStringFromJson(String json,String key) throws JSONException
	{
		JSONTokener parser = new JSONTokener(json);
		JSONObject obj = (JSONObject)parser.nextValue();
		
		String value = obj.getString(key);
		
		return value;
	}
	
}
