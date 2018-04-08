package net.monkeystudio.base.utils;

import java.math.BigDecimal;

/**
 * Created by bint on 15/03/2018.
 */
public class BigDecimalUtils {

    public static BigDecimal getInstance(Float f){

        return getInstance(f.toString());

    }

    public static BigDecimal getInstance(String str){
        return new BigDecimal(str);
    }


    public static BigDecimal getInstance(Double d){
        return new BigDecimal(d.toString());
    }

}
