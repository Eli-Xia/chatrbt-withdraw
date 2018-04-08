package net.monkeystudio.base.utils;

import java.math.BigDecimal;

/**
 * Created by xiaxin
 */
public class FloatArithmeticUtil {

    private FloatArithmeticUtil(){

    }

    public static int compare2FloatArgs(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.compareTo(b2);
    }

    public static BigDecimal float2BigDecimal(float v){
        BigDecimal b = new BigDecimal(Float.toString(v));
        return b;
    }

    public static BigDecimal add(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.subtract(b2);
    }


    public static BigDecimal mul(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal div(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入,保留2位小数

        //除不尽的情况
    }






}
