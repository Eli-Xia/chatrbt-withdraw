package net.monkeystudio.wx.service;

import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URL;

/**
 * 微信企业付款相关工具类
 */
@Service
public class WxTransferKitService {
    private static String MCH_APPID;//appid
    private static String MCHID;//商户id
    private static String CERT_PATH;//证书路径
    private static String SIGN_KEY;//签名算法秘钥

    @Autowired
    private CfgService cfgService;

    @PostConstruct
    public void init(){
        MCH_APPID = cfgService.get(GlobalConfigConstants.MCH_APPID_KEY);
        MCHID = cfgService.get(GlobalConfigConstants.MCHID_KEY);
        CERT_PATH = cfgService.get(GlobalConfigConstants.CERT_PATH_KEY);
        SIGN_KEY = cfgService.get(GlobalConfigConstants.SIGN_KEY_KEY);
    }


    public static void main(String []args){
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        System.out.println(path);
    }

}
