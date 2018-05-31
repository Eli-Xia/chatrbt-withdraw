package net.monkeystudio.chatrbtw.utils;

import java.util.Date;

/**
 * Created by bint on 2018/5/26.
 */
public class ChatPetMissionNoUtil {

    public static String getMissionNo(Date date){
        Long time = date.getTime();
        String no = String.valueOf(time/1000 % 10000 );

        if(no.length() == 3){
            no = "0" + no;
        }

        return "NO." + no;
    }
}
