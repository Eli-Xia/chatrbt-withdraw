package net.monkeystudio.chatrbtw.service;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.bean.miniapp.MiniAppFanBaseInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
public class MiniAppUserInfoService {

    @Autowired
    private WxFanService wxFanService;

    public void reviseMiniAppFan(Integer fanId,String rawData,String encryptedData,String iv,String signature){
        WxFan wxFan = wxFanService.getById(fanId);

        if(wxFan == null){
            return;
        }



    }

    public Object getUserInfo(String rawData,String encryptedData,String iv,String signature,String sessionKey)throws Exception{
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
                return JsonUtil.readValue(result, MiniAppFanBaseInfo.class);
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    public static void main(String[]args){
        Map<String,Object> map = new HashMap<>();
        map.put("openId","OPENID");
        map.put("nickName","NICKNAME");
        map.put("gender","GENDER");
        map.put("city","CITY");
        map.put("province","PROVINCE");
        map.put("country","COUNTRY");
        map.put("avatarUrl","AVATARURL");
        map.put("unionId","UNIONID");
        Map<String,Object> map2= new HashMap<>();
        map2.put("appid","APPID");
        map2.put("timestamp",new Date().getTime());
        map.put("watermark",map2);

        String s = JsonUtil.toJSon(map);

        MiniAppFanBaseInfo miniAppFanBaseInfo = JsonUtil.readValue(s, MiniAppFanBaseInfo.class);

        System.out.println(s);
        System.out.println(1);
    }
}
