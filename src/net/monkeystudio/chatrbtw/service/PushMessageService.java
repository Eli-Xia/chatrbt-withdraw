package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.entity.ChatLog;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.wx.service.WxPubService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2017/11/12.
 */
@Service
public class PushMessageService {

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private WxCustomerHelper wxCustomerHelper;

    @Autowired
    private WxPubService wxPubService;

    private final static Long RECOVER_PERIOD = 48 * 60 * 60L;


    public void checkNeedToSendMessageHandle(){
        String needToSend = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_RECOVER_MESSAGE_SWITCH_KEY);

        if(needToSend == null){
            return ;
        }

        //FIXME
        if(needToSend.equals("1")){
            pushRecoverMessage();
        }

    }

    /**
     * 推送挽回粉丝消息
     */
    private void pushRecoverMessage(){

        String content = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_RECOVER_CONTENT_KEY);

        //如果推送内容为空，则不推送
        if(content == null){
            return ;
        }

        Long currentTime = TimeUtil.getCurrentTimestamp();
        Long endTime = currentTime - RECOVER_PERIOD;

        Long startTime = endTime - 30 * 60;
        List<ChatLog> before = chatLogService.getByCreateTime(startTime, endTime);

        //先过滤掉半个小时内重复的
        List<ChatLog> after = new ArrayList<>();
        for(ChatLog chatLogFromBefore : before){

            String userOpenIdBefore = chatLogFromBefore.getUserOpenid();
            String wxPubOriginIdBefore = chatLogFromBefore.getWxPubOriginId();

            Boolean isExist = false;

            for(ChatLog chatLogFromAfter : after){

                String userOpenIdAfter = chatLogFromAfter.getUserOpenid();
                String wxPubOriginIdAfter = chatLogFromAfter.getWxPubOriginId();

                if(userOpenIdBefore.equals(userOpenIdAfter) && wxPubOriginIdBefore.equals(wxPubOriginIdAfter)){
                    isExist = true;
                }
            }

            if(isExist){
                continue;
            }else {
                after.add(chatLogFromBefore);
            }
        }

        //过滤掉48小时内还继续有联系的
        List<ChatLog> result = new ArrayList<>();
        for(ChatLog chatLog : after){

            String userOpenIdAfter = chatLog.getUserOpenid();
            String wxPubOriginIdAfter = chatLog.getWxPubOriginId();

            Integer count = chatLogService.countByTime(startTime,endTime,wxPubOriginIdAfter,userOpenIdAfter);

            if(count == 0){
                result.add(chatLog);
            }
        }

        //推送消息
        for(ChatLog chatLog : result){
            String userOpenId = chatLog.getUserOpenid();
            String wxPubOriginId = chatLog.getWxPubOriginId();

            WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

            String wxOpenId = wxPub.getAppId();
            wxCustomerHelper.sendTextMessageByAuthorizerId(userOpenId ,wxOpenId,content);
        }
    }

}
