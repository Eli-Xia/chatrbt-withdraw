package net.monkeystudio.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bint on 07/02/2018.
 */
public class HtmlTagUtil {

    public static String generateATag(String url , String text){

        StringBuffer sb = new StringBuffer();

        sb.append("<a href='" + url + "'>" + text + "</a>");

        return sb.toString();
    }

    /**
     * 获取a标签的href
     * @param content
     * @return
     */
    public static String getATagHref(String content){

        Pattern pattern = null;

        if(content.contains("'")){
            pattern = Pattern.compile("href\\s*=\\s*(?:\'([^\']*)\'|'([^']*)'|([^\'>\\s]+))");
        }else {
            pattern = Pattern.compile("href\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|\'([^\']*)\'|([^\"'>\\s]+))");
        }
        Matcher matcher = pattern.matcher(content);

        if(matcher.find()){
            return matcher.group(1);
        }

        return null;
    }

}
