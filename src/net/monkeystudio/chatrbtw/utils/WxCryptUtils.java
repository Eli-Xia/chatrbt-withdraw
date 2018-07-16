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
        String encrypt = "AvCgniraqHy4wo4/PVvdn48yTQSJClKdjkbdGyEdpUhBWR5Sitpb/tg4Vdpk6Sh8TOn8eyrsKPcItGYtaYPKwMNEAHnpE/Kj0SwWWW0zjHBx/0ZukLnQdsFPTTStkVzI8qPKzyNRsSUgAI6M1+4z2hsmJpfCJlOcyeqkoAdcSYtJmoG1Kdnasn8m5HGRK3b1J6hF/Qpcf7mXSv6NMKQw223o/JL1gj8xvGuag4qXt9mNV3cKEW51GUehaUypafZavV76L+EVR0dMcK4IMhio407QYIgNVFLAADkFrvvAG2oJflCdhmtfs+ovZ8Rn8PxbsP8FR3tDbG/1XVLDCg8UL9uJwxYceIZns1+UBwmIwTSnC2ZR74s80mZDCBLqbynNLEito2AIt0Xv2Zj/wW8br5qhWj43jj9jDKHLpf6FTRMUb7jkNBHJnAmp2iZwDH/ZpSLblIB/TpSpjQHg4VT5pE/UoIoICHS4slRZ7M4QBhA=";

        String sessionKey = "dG1npZSgP81BIN7iK3cPAQ==";
        String iv = "bVTZUIkUtOF1n5BttqkGVw==";


        String decrypt = WxCryptUtils.decrypt(encrypt, iv, sessionKey);
        System.out.println(decrypt);
        System.out.println(1);
    }
}