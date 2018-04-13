package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.HtmlTagUtil;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.sdk.wx.bean.SubscribeEvent;
import net.monkeystudio.wx.controller.bean.TextMsgRec;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.mp.aes.XMLParse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2017/12/11.
 */
@Service
public class WxEventMessageHandler {

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private RWxPubProductService rWxPubProductService;

    private final static String SUBSCRIBE_EVENT = "subscribe";

    public String handleEvent(String content){
        String eventType = this.judgeEventType(content);

        if(SUBSCRIBE_EVENT.equals(eventType)){

            SubscribeEvent subscribeEvent = XmlUtil.converyToJavaBean(content, SubscribeEvent.class);
            String wxPubOriginId = subscribeEvent.getToUserName();

            if(rWxPubProductService.isEnable(ProductService.CHAT_PET ,wxPubOriginId)){
                TextMsgRes textMsgRes = new TextMsgRes();

                textMsgRes.setCreateTime(TimeUtil.getCurrentTimestamp());

                String scene_str = subscribeEvent.getEventKey();

                String repltContent = "您的宠物已经生成，";

                String aTag = HtmlTagUtil.generateATag("www.baidu.com", "点击查看");
                textMsgRes.setContent(repltContent + aTag);
                textMsgRes.setMsgType("text");

                String fromUserName = subscribeEvent.getFromUserName();
                textMsgRes.setToUserName(fromUserName);

                String toUserName = subscribeEvent.getToUserName();
                textMsgRes.setFromUserName(toUserName);

                return XmlUtil.convertToXml(textMsgRes);
            }


            if(rWxPubProductService.isEnable(ProductService.ASK_SEARCH, wxPubOriginId)){
                TextMsgRes textMsgRes = new TextMsgRes();

                textMsgRes.setCreateTime(TimeUtil.getCurrentTimestamp());
                textMsgRes.setContent("欢迎关注我们公众号，回复关键字，即可获取我们公众号的过往历史文章！");
                textMsgRes.setMsgType("text");

                String fromUserName = subscribeEvent.getFromUserName();
                textMsgRes.setToUserName(fromUserName);

                String toUserName = subscribeEvent.getToUserName();
                textMsgRes.setFromUserName(toUserName);

                return XmlUtil.convertToXml(textMsgRes);
            }

            if(rWxPubProductService.isEnable(ProductService.SMART_CHAT, wxPubOriginId)){
                TextMsgRes textMsgRes = new TextMsgRes();

                textMsgRes.setCreateTime(TimeUtil.getCurrentTimestamp());
                textMsgRes.setContent("欢迎关注我们公众号，我可以陪你聊天哟！！！么么哒～～");
                textMsgRes.setMsgType("text");

                String fromUserName = subscribeEvent.getFromUserName();
                textMsgRes.setToUserName(fromUserName);

                String toUserName = subscribeEvent.getToUserName();
                textMsgRes.setFromUserName(toUserName);

                return XmlUtil.convertToXml(textMsgRes);
            }


        }

        return null;
    }

    public String testEventHandle(String content){
        String eventType = this.judgeEventType(content);

        TextMsgRes textMsgRes = new TextMsgRes();

        textMsgRes.setCreateTime(TimeUtil.getCurrentTimestamp());
        textMsgRes.setContent(eventType + "from_callback");
        textMsgRes.setMsgType("text");

        String fromUserName = XMLParse.extractField(content, "FromUserName");
        textMsgRes.setToUserName(fromUserName);

        String toUserName = XMLParse.extractField(content, "ToUserName");
        textMsgRes.setFromUserName(toUserName);

        return XmlUtil.convertToXml(textMsgRes);
    }
    /**
     * 判断事件种类
     * @param str
     * @return
     */
    private String judgeEventType(String str){

        String eventType = XMLParse.extractField(str, "Event");
        return eventType;
    }
}
