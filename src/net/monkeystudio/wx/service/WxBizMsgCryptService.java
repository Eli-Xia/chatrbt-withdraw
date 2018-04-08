package net.monkeystudio.wx.service;

import net.monkeystudio.wx.mp.aes.AesException;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.wx.mp.aes.WXBizMsgCrypt;
import net.monkeystudio.wx.mp.beam.ComponentVerifyTicket;
import net.monkeystudio.wx.vo.pub.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by bint on 2017/11/3.
 */

@Service
public class WxBizMsgCryptService {

    private WXBizMsgCrypt wxBizMsgCrypt ;

    @Autowired
    private CfgService cfgService;

    @PostConstruct
    private void initWXBizMsgCrypt(){

        String componentAppId = cfgService.get(GlobalConfigConstants.COMPONENT_APP_ID_KEY);
        String vaidatedTokenKey = cfgService.get(GlobalConfigConstants.VAIDATED_TOKEN_KEY_KEY);
        String encodingAesKey = cfgService.get(GlobalConfigConstants.ENCODING_AES_KEY);

        try {
            wxBizMsgCrypt = new WXBizMsgCrypt(vaidatedTokenKey,encodingAesKey,componentAppId);
        } catch (AesException e) {
            e.printStackTrace();
        }
    }

    /**
     * @deprecated
     * @param postData
     * @return
     */
    public ComponentVerifyTicket decryptTicke(String postData){
        return wxBizMsgCrypt.decryptTicker(postData);
    }


    public TextMessage decryptTextMessage(String msgSignature, String timeStamp, String postData){
        return wxBizMsgCrypt.decryptTextMessage(postData);
    }

    public String decryptEvent(String postData){
        return wxBizMsgCrypt.decryptEventString(postData);
    }

    public String encrypt(String replyMsg, String timestamp, String nonce){
        String encrypt = null;
        try {
            encrypt = wxBizMsgCrypt.encryptMsg(replyMsg,timestamp,nonce);
        } catch (AesException e) {
            e.printStackTrace();
        }

        return encrypt;
    }


    /**
     *
     * @param txt
     * @return
     */
    public String decrypt(String txt){
        try {
            String result = wxBizMsgCrypt.decrypt(txt);
            return result;
        } catch (AesException e) {
            e.printStackTrace();
        }
        return null;
    }
}
