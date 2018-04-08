package net.monkeystudio.base.utils;

import java.util.List;

/**
 * Created by bint on 27/03/2018.
 */
public class ListUtil {

    /**
     * 判断list是否为空
     * @param list
     * @return
     */
    public static Boolean isEmpty(List<?> list){

        if(list == null || list.size() == 0){
            return true;
        }

        return false;
    }


    /**
     * 判断list是否不为空
     * @param list
     * @return
     */
    public static Boolean isNotEmpty(List<?> list){

        return !isEmpty(list);
    }


}
