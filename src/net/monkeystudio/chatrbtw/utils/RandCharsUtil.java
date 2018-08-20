package net.monkeystudio.chatrbtw.utils;

import java.text.SimpleDateFormat;
import java.util.Random;

public class RandCharsUtil {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

        Random random = new Random();

        StringBuffer sb = new StringBuffer();

        int number = 0;

        for (int i = 0; i < length; i++) {
            number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
