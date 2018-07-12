package net.monkeystudio.chatrbtw.utils;

import java.math.BigDecimal;

/**
 * Created by bint on 2018/7/10.
 */
public class BigDecimalUtil {

    /**
     * 去除小数点后极为
     * 例子：输入1.234和2，输出1.24
     * @param bigDecimal
     * @param decimalPoint 保留的小数点位数
     * @return
     */
    public static BigDecimal dealDecimalPoint(BigDecimal bigDecimal,Integer decimalPoint){
        BigDecimal reusult = bigDecimal.setScale(decimalPoint, BigDecimal.ROUND_DOWN);
        return reusult;
    }

}
