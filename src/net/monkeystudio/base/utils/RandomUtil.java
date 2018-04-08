package net.monkeystudio.base.utils;

import java.util.Random;

/**
 * 随机工具类
 * Created by bint on 2017/11/15.
 */
public class RandomUtil {

    /**
     * 是否命中
     * @param probability 概率
     * @return
     */
    public static Boolean shot(Float probability){

        if(probability > 1F){
            return true;
        }

        Float factor = probability * 10000;

        Random random = new Random();
        Integer randomInt = random.nextInt(10000);

        if(randomInt < factor.intValue()){
            return true;
        }

        return false;
    }

    // nextInt取值:[0,x)
    public static int randomIndex(int size){
        Random random = new Random();
        return random.nextInt(size);
    }
}
