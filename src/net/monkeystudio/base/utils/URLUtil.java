package net.monkeystudio.base.utils;

/**
 * url工具类
 * Created by bint on 2017/12/18.
 */
public class URLUtil {
    private final static String QUESTION_MARK = "?";
    private final static String AMPERSAND = "&";
    private final static String EQUAL_SIGN = "=";
    private final static String FORWARD_SLASH = "/";

    public static String addParam(String url , String paramName ,String paramValue){

        if(url  == null || paramName == null || paramValue == null){
            return null;
        }

        Integer questionMarkIndex = url.indexOf(QUESTION_MARK);
        if(questionMarkIndex != -1){
            url = url + AMPERSAND + paramName + EQUAL_SIGN + paramValue;
        }else {
            url = url + QUESTION_MARK + paramName + EQUAL_SIGN + paramValue;
        }

        return url;
    }

    public static String addPathParam(String url ,String paramValue){
        if(url  == null || paramValue == null){
            return null;
        }

        if (url.endsWith(FORWARD_SLASH)){
            url = url + paramValue;
        }else {
            url = url + FORWARD_SLASH + paramValue;
        }

        return url;
    }



    public static String getParam(String url ,String paramName){

        if(url.indexOf("?") == -1){
            return null;
        }

        String[] strings = url.split("\\?");


        String string1 = strings[1];

        String[] params = string1.split("\\&");


        for(String param : params){

            String[] strs = param.split("\\=");

            String name = strs[0];

            String value = null;
            if(strs.length != 1){
                value = strs[1];
            }

            if(name.equals(paramName)){
                return value;
            }

        }

        return null;
    }


    public static void main(String[] args) {
        String url = "www.baidu.com?name=";

        System.out.println(getParam(url,"name"));
    }
}
