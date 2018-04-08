package net.monkeystudio.base.utils;

/**
 * Created by bint on 27/03/2018.
 */
import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 对url加密的加密解密算法，这样的加密结果只有数字和字母
 * @author Administrator
 *
 */
public class Base64EncodingUtil {
    private static final BASE64Decoder decoder = new BASE64Decoder();
    private static final BASE64Encoder encoder = new BASE64Encoder();
    private static final Base64 base64 = new Base64();

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(String key) throws Exception {
        if (key == null || key.length() < 1) {
            return "";
        }
        return new String(base64.encodeBase64URLSafe((new String(encoder.encode(key.getBytes()))).getBytes()));
    }


    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptBASE64(String key) throws Exception {
        if (key == null || key.length() < 1) {
            return "";
        }

        return new String(decoder.decodeBuffer(new String(base64.decodeBase64(key.getBytes()))));
    }

    public static void main(String[] args) throws Exception {
        String s = Base64EncodingUtil.encryptBASE64("userId=3&chat=null");
        //System.out.println(s);
        System.out.println(Base64EncodingUtil.decryptBASE64("UDNWelpYSkpaRDAwSm5OdmRYSmpaVDB3Sm1Ob1lYUmliM1JKWkQweE9EVT0"));
    }
}