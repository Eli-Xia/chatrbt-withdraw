package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HtmlTagUtil;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.EthnicGroups;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.sdk.wx.bean.SubscribeEvent;
import net.monkeystudio.chatrbtw.service.bean.ethnicgroupscode.EthnicGroupsCodeValidatedResp;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.mp.aes.XMLParse;

import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2017/12/11.
 */
@Service
public class WxEventMessageHandler extends WxBaseMessageHandler {

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private CfgService cfgService;

    @Autowired
    private WxFanService wxFanService;



    private final static String SUBSCRIBE_EVENT = "subscribe";

    public String handleEvent(String content) {
        String eventType = this.judgeEventType(content);

        if (SUBSCRIBE_EVENT.equals(eventType)) {

            SubscribeEvent subscribeEvent = XmlUtil.converyToJavaBean(content, SubscribeEvent.class);
            String wxPubOriginId = subscribeEvent.getToUserName();
            String wxFanOpenId = subscribeEvent.getFromUserName();

            //如果有启用陪聊宠
            if (rWxPubProductService.isEnable(ProductService.CHAT_PET, wxPubOriginId)) {

                String qrSceneStr = subscribeEvent.getEventKey();

                //如果EventKey不为空
                if (StringUtil.isNotEmpty(qrSceneStr)) {


                    //判断是否包含特地字符串，以免有其他平台也在用该接口
                    if (qrSceneStr.contains(EthnicGroupsService.EVENT_SPECIAL_STR)) {

                        String parentIdStr = qrSceneStr.replace("qrscene_" + EthnicGroupsService.EVENT_SPECIAL_STR, "");

                        Integer chatPetId = null;
                        ChatPet chatPet = null;
                        ChatPet parentChatPet = null;
                        if (ethnicGroupsService.isNotFounderEventKey(parentIdStr)) {
                            Integer parentId = Integer.valueOf(parentIdStr);

                            parentChatPet = chatPetService.getById(parentId);

                            //如果不是长老，
                            if (chatPet == null) {
                                String replyContent = "链接有误，请检查链接参数";

                                return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
                            }

                            chatPetId = chatPetService.generateChatPet(wxPubOriginId, wxFanOpenId, parentId, chatPet.getSecondEthnicGroupsId());
                        } else {
                            Integer ethnicGroupsId = ethnicGroupsService.createSecondEthnicGroups(wxPubOriginId, wxFanOpenId);

                            EthnicGroups ethnicGroups = ethnicGroupsService.getFounderEthnicGroups(wxPubOriginId);

                            chatPetService.generateChatPet(wxPubOriginId, wxFanOpenId, ethnicGroups.getId(), ethnicGroupsId);
                        }

                    /*EthnicGroupsCodeValidatedResp ethnicGroupsCodeValidatedResp = ethnicGroupsService.validated(chatPet.getId(),wxPubOriginId);

                    if(ethnicGroupsCodeValidatedResp.getStatus().intValue() != EthnicGroupsService.ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ENABLE.intValue()){
                        String repltContent = ethnicGroupsCodeValidatedResp.getContent();

                        textMsgRes.setContent(repltContent);
                        textMsgRes.setMsgType("text");

                        String fromUserName = subscribeEvent.getFromUserName();
                        textMsgRes.setToUserName(fromUserName);

                        String toUserName = subscribeEvent.getToUserName();
                        textMsgRes.setFromUserName(toUserName);
                        return XmlUtil.convertToXml(textMsgRes);
                    }*/

                        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId,wxFanOpenId);


                        String parentWxFanNickname = null;
                        if(parentChatPet != null){
                            String parentWxFanOpenId = parentChatPet.getWxFanOpenId();
                            WxFan parentWxFan = wxFanService.getWxFan(wxPubOriginId, parentWxFanOpenId);
                            parentWxFanNickname = parentWxFan.getNickname();
                        }else {
                            parentWxFanNickname = "Wedo";
                        }




                        CustomerNewsItem customerNewsItem = new CustomerNewsItem();
                        String description = wxFan.getNickname() + "的斑马，出生于2018年4月12日8点30分您已经成功接受# " + parentWxFanNickname + "#的邀请，创造了一只独一无二的斑马，\n" +
                                "加入了斑马星球。\n" +
                                "\n" +
                                "斑马星球的基本规律：多爱心互动，多真诚聊天\n" +
                                "等到长大，虚拟宠物带回现实Money，报效宠爸宠妈\n" +
                                "\n" +
                                "聊天中会随机触发事件\n" +
                                "每一次点击链接，完成每日任务，即获得成长经验值";
                        customerNewsItem.setDescription(description);

                        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
                        String uri = "/res/wedo/zebra.html?id=" + chatPetId;
                        String url = domain + uri;
                        customerNewsItem.setUrl(url);
                        customerNewsItem.setPicUrl("http://" + domain + "/res/wedo/images/normal_cover.jpg");

                        String replyContent = "您的宠物已经出生！";
                        customerNewsItem.setTitle(replyContent);

                        return this.replySingleNewsStr(wxPubOriginId, wxFanOpenId, customerNewsItem);

                    }
                }
            }

            //如果有开启问问搜
            if (rWxPubProductService.isEnable(ProductService.ASK_SEARCH, wxPubOriginId)) {

                String replyContent = "欢迎关注我们公众号，回复关键字，即可获取我们公众号的过往历史文章！";

                return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
            }

            //如果有开启智能聊
            if (rWxPubProductService.isEnable(ProductService.SMART_CHAT, wxPubOriginId)) {

                String replyContent = "欢迎关注我们公众号，我可以陪你聊天哟！！！么么哒～～";

                return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
            }

        }

        return null;
    }

    public String testEventHandle(String content) {
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


    private String replySingleNewsStr(String wxPubOriginId, String wxFanOpendId, CustomerNewsItem customerNewsItem) {

        List<CustomerNewsItem> customerNewsList = new ArrayList<>();

        customerNewsList.add(customerNewsItem);

        return this.replyNewsStr(wxPubOriginId, wxFanOpendId, customerNewsList);

    }

    /**
     * 判断事件种类
     *
     * @param str
     * @return
     */
    private String judgeEventType(String str) {

        String eventType = XMLParse.extractField(str, "Event");
        return eventType;
    }
}
