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
        String encrypt = "cNDm95j7Zafm51C3JWEuVvXPhnAVw9fo1Uv3NqeF/zz9FuyfHE6Xs2CmWrOlJPkWWwCyaACUq+nBZldhimjrQGR9L4dLGljRi+SfX2u3xi8GJBIQqPCcv0LHlTwsf1XGvT/xMzEwyLm7hyRhUCxh68szwZ29sipJ77+fzp7gFwb73EmMkW8+ekyi3oVP83UldwH7usUcSTk8bRH2KXJriiDhJ18fy3ABJD8KoV18xka7zf1X40MF1FVDuiyVnHC3+Qn0zgTjDaFbHO2q9mG3wG4GZ1+xTkIZS5scvEVJ9Q3KWfFzeWmHQLYRNCugS5C36zxdd2enLC9KOJ+vngxRkZsV4jiIZ2U83ii5h2Cq+jMAdgcxC84I/gAOUhLemsoZm05j1rmzh5GlyITWiie3yarkDRb3A3mzkz/w3mE+Dkr+I3J+XJ2qqMO5KAimQTWAfRrrLfg/NJUUE0xYUzAStl+xGhtxYf8wLwXqi74elB0=";
        String sessionKey = "9qMz5/+ss+GCtg7OdMrVDw==";
        String iv = "fOWCLUI72Vw1U7mEwiyGUw==";


        String decrypt = WxCryptUtils.decrypt(encrypt, iv, sessionKey);
        System.out.println(decrypt);
        System.out.println(1);
    }
}