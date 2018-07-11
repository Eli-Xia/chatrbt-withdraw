package net.monkeystudio.chatrbtw.service;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.bean.miniapp.MiniProgramFanBaseInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaxin
 */
@Service
public class MiniProgramUserInfoService {

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    /**
     * 完善小程序用户信息
     * @param fanId             小程序粉丝id
     * @param rawData
     * @param encryptedData
     * @param iv
     * @param signature
     * @throws Exception
     */
    public void reviseMiniProgramFan(Integer fanId,String rawData,String encryptedData,String iv,String signature)throws Exception{
        WxFan wxFan = wxFanService.getById(fanId);

        if(wxFan == null){
            return;
        }

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader("token");

        if(token != null){
            String value = redisCacheTemplate.getString(token);
            String sessionKey = value.split("\\+")[1];
            MiniProgramFanBaseInfo miniProgramFanBaseInfo = this.getMiniProgramFanBaseInfo(rawData, encryptedData, iv, signature, sessionKey);

            wxFan.setCity(miniProgramFanBaseInfo.getCity());
            wxFan.setProvince(miniProgramFanBaseInfo.getProvince());
            wxFan.setCountry(miniProgramFanBaseInfo.getCountry());
            wxFan.setNickname(miniProgramFanBaseInfo.getNickname());
            wxFan.setHeadImgUrl(miniProgramFanBaseInfo.getHeadImgUrl());
            wxFan.setSex(miniProgramFanBaseInfo.getSex());
            wxFan.setUnionId(miniProgramFanBaseInfo.getUnionId());

            wxFanService.update(wxFan);
        }
    }


    /**
     * 解密小程序用户信息
     * @param rawData
     * @param encryptedData
     * @param iv
     * @param signature
     * @param sessionKey
     * @return
     * @throws Exception
     */
    public MiniProgramFanBaseInfo getMiniProgramFanBaseInfo(String rawData, String encryptedData, String iv, String signature, String sessionKey)throws Exception{
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JsonUtil.readValue(result, MiniProgramFanBaseInfo.class);
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

}
