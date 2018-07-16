package net.monkeystudio.chatrbtw.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;


/**
 * @author xiaxin
 */
public class WxCryptUtils {

    /**
     * 小程序 数据解密
     *
     * @param encryptData 加密数据
     * @param iv          对称解密算法初始向量
     * @param sessionKey  对称解密秘钥
     * @return 解密数据
     */
    public static String decrypt(String encryptData, String iv, String sessionKey) throws Exception {
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("AES");
        algorithmParameters.init(new IvParameterSpec(Base64.decodeBase64(iv)));
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(sessionKey), "AES"), algorithmParameters);
        byte[] decode = PKCS7Encoder.decode(cipher.doFinal(Base64.decodeBase64(encryptData)));
        String decryptStr = new String(decode, StandardCharsets.UTF_8);
        return decryptStr;
    }

    /**
     * 数据加密
     *
     * @param data       需要加密的数据
     * @param iv         对称加密算法初始向量
     * @param sessionKey 对称加密秘钥
     * @return 加密数据
     */
    public static String encrypt(String data, String iv, String sessionKey) throws Exception {
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("AES");
        algorithmParameters.init(new IvParameterSpec(Base64.decodeBase64(iv)));
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(sessionKey), "AES"), algorithmParameters);
        byte[] textBytes = data.getBytes(StandardCharsets.UTF_8);
        ByteGroup byteGroup = new ByteGroup();
        byteGroup.addBytes(textBytes);
        byte[] padBytes = PKCS7Encoder.encode(byteGroup.size());
        byteGroup.addBytes(padBytes);
        byte[] encryptBytes = cipher.doFinal(byteGroup.toBytes());
        return Base64.encodeBase64String(encryptBytes);
    }


    public static void main(String[] args) throws Exception {
        // 微信 小程序的 测试数据
        String encrypt = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZM\n" +
                "                QmRzooG2xrDcvSnxIMXFufNstNGTyaGS\n" +
                "                9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+\n" +
                "                3hVbJSRgv+4lGOETKUQz6OYStslQ142d\n" +
                "                NCuabNPGBzlooOmB231qMM85d2/fV6Ch\n" +
                "                evvXvQP8Hkue1poOFtnEtpyxVLW1zAo6\n" +
                "                /1Xx1COxFvrc2d7UL/lmHInNlxuacJXw\n" +
                "                u0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn\n" +
                "                /Hz7saL8xz+W//FRAUid1OksQaQx4CMs\n" +
                "                8LOddcQhULW4ucetDf96JcR3g0gfRK4P\n" +
                "                C7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB\n" +
                "                6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns\n" +
                "                /8wR2SiRS7MNACwTyrGvt9ts8p12PKFd\n" +
                "                lqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYV\n" +
                "                oKlaRv85IfVunYzO0IKXsyl7JCUjCpoG\n" +
                "                20f0a04COwfneQAGGwd5oa+T8yO5hzuy\n" +
                "                Db/XcxxmK01EpqOyuxINew==";

        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
        String iv = "r7BXXKkLb8qrSNn05n0qiA==";

        String decrypt = WxCryptUtils.decrypt(encrypt, iv, sessionKey);
        System.out.println(decrypt);
    }
}