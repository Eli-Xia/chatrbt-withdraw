package net.monkeystudio.base.utils;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by bint on 2017/10/27.
 */
public class StringUtil {

    private static final String UDER_LINE = "_";


    public static Boolean isEmpty(String str){

        if(str == null || "".equals(str)){
            return true;
        }

        return false;
    }


    public static Boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    public static String formatString(String string ){

        Integer index = string.indexOf(UDER_LINE);

        while (index != -1){

            String letter = string.substring(index + 1 , index + 2);

            String capitalLetter = letter.toUpperCase();

            string = string.replaceFirst(UDER_LINE + letter , capitalLetter);

            index = string.indexOf(UDER_LINE);

        }

        return string ;
    }


    public static String readBuffer(BufferedReader br){

        if(br == null){
            return null;
        }

        String inputLine;
        StringBuffer body = new StringBuffer();
        try {
            while ((inputLine = br.readLine()) != null) {
                body.append(inputLine);
            }
        } catch (IOException e) {
            Log.e("IOException: " + e);
            return "";
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return body.toString();
    }

    /**
     * 分割字符串
     * @param str:原字符串
     * @param delimiter:分割符
     * @return
     */
    public static String[] delimite(String str,String delimiter){
        return StringUtils.delimitedListToStringArray(str,delimiter);
    }


}
