package net.monkeystudio.wx.utils;

/**
 * Created by bint on 2017/11/9.
 */
public class WxTextContentResponseUtil {

    private static final String A_TAG = "<a href =\"#{href}\">#{content}</a>";

    public static String setHyperlink(String content,String target , String url){

        Integer index = content.indexOf(target);

        if(index == -1){
            return content;
        }

        Integer targetLength = target.length();

        String str = content.substring(0,index );
        String str2 = content.substring(index, index + targetLength);
        String str3 = content.substring(index + targetLength, content.length());


        String ATageNew = A_TAG.replace("#{href}",url);
        ATageNew = ATageNew.replace("#{content}", str2);

        String result = str + ATageNew + str3;

        return result;
    }


}
