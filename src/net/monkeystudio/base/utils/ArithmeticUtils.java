package net.monkeystudio.base.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linhongbin on 2017/6/1.
 */
public class ArithmeticUtils {

    public static Double divide(Long divisor ,Long divide){
        BigDecimal divisorBigDecimal = new BigDecimal(divisor);
        BigDecimal divideBigDecimal = new BigDecimal(divide);

        BigDecimal result = divide(divisorBigDecimal,divideBigDecimal);

        return result.doubleValue();

    }


    public static BigDecimal getAverage(List<Long> list){
        List<BigDecimal> bigDecimalList = new ArrayList<>();
        for (Long l : list){
            BigDecimal bigDecimal = new BigDecimal(l);

            bigDecimalList.add(bigDecimal);
        }

        BigDecimal result = getAverageByBigDecimal(bigDecimalList);

        return result;
    }

    public static BigDecimal getAverageByBigDecimal(List<BigDecimal> list){
        BigDecimal divisorBigDecimal = new BigDecimal(0);
        for(BigDecimal bigDecimal : list){
            divisorBigDecimal = divisorBigDecimal.add(bigDecimal);
        }

        BigDecimal result = ArithmeticUtils.divide(divisorBigDecimal,new BigDecimal(list.size()));

        return result;
    }


    public static BigDecimal divide(BigDecimal divisor ,BigDecimal divide){

        if(divide.floatValue() == 0){
            return null;
        }

        BigDecimal result = divisor.divide(divide,10, BigDecimal.ROUND_HALF_UP);


        return result;
    }


    public static BigDecimal multiply(BigDecimal multiplier ,BigDecimal multiplicative){
        return multiplier.multiply(multiplicative);
    }


    public static Long getMax(List<Long> longList){

        if(longList == null || longList.size() == 0){
            return null;
        }
        Long max = longList.get(0);

        for(Long l : longList){

            if(l > max){
                max = l;
            }
        }

        return max;
    }

    public static String keep2DecimalPlace(Float value){
        DecimalFormat df = new DecimalFormat("#.00");
        String format = df.format(value);
        return format;
    }
}
