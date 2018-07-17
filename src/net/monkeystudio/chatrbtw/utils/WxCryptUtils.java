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
        String encrypt = "RD5SOXuGySnwbgAyIDBBnENIuG5zTJum/yfZkpYhYSavTGOjvqU107v8f796cMJe/8TWNdTyCUC5Gib8xezgZBKLFuYmC61o/9N9DxqVEptqS9IUDbv+ehVK++TRoUzJa+47LB6/l+6vvCXEAlLLYjSC49l+j/iaKrSJyUD9wDBrhGHqRUUvEbOlkSlz94644uvFp44++SB0ZptuhOvEgyeksarp8k0r3HDHmC9djJz4uo/PQDHjcINI3NTb13At6TBkCQ26UCqTsjFdcABGVk8LZ1xpzLZstdBKwmfgufAeIK4jU0sVYQqw4fYLXOZipExRaJhkOkjiYuyDauTi8jck+LhaHuQ6M1ymhyLXaQv1MRZvO7hAR8XICarahOM/PBSBlvcjR9Qx1nYQRaJp0n88kwhkKF8xus+pAyANs7d7VK3A9tq4cyncYj00W9f0C8IbLqoOwCBWbPVuWckRm4dAdjcEpGtntSklmwOIZWA=";
        String sessionKey = "eRntw4roMb+27YeDplWKqQ==";
        String iv = "dtBNiGJuQro1gxaskISVuQ==";


        String decrypt = WxCryptUtils.decrypt(encrypt, iv, sessionKey);
        System.out.println(decrypt);
        System.out.println(1);
    }
}