package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HtmlTagUtil;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.EthnicGroups;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.sdk.wx.bean.SubscribeEvent;
import net.monkeystudio.chatrbtw.service.bean.ethnicgroupscode.EthnicGroupsCodeValidatedResp;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.mp.aes.XMLParse;

import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2017/12/11.
 */
@Service
public class WxEventMessageHandler extends WxBaseMessageHandler {

    @Autowired
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private ChatPetService chatPetService;


    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetLogService chatPetLogService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private CfgService cfgService;



    private final static String SUBSCRIBE_EVENT = "subscribe";
    private final static String SCAN_EVENT = "scan";

    public String handleEvent(String content) {
        String eventType = this.judgeEventType(content);

        eventType = eventType.toLowerCase();

        if (SUBSCRIBE_EVENT.equals(eventType) || SCAN_EVENT.equals(eventType)) {

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

                        ChatPet chatPet = chatPetService.getChatPetByFans(wxPubOriginId,wxFanOpenId);

                        //已经有宠物的，不做处理
                        if(chatPet != null){
                            return null;
                        }
                        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);

                        if(!ethnicGroupsService.allowToAdopt(wxPubOriginId)){
                            String replyContent = "今日宠物已经领完了，明天早点来哟！";
                            return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
                        }

                        String parentIdStr = null;
                        if(qrSceneStr.indexOf("qrscene_" + EthnicGroupsService.EVENT_SPECIAL_STR) != -1){
                            parentIdStr = qrSceneStr.replace("qrscene_" + EthnicGroupsService.EVENT_SPECIAL_STR, "");
                        }

                        if(qrSceneStr.indexOf(EthnicGroupsService.EVENT_SPECIAL_STR) != -1){
                            parentIdStr = qrSceneStr.replace(EthnicGroupsService.EVENT_SPECIAL_STR, "");
                        }

                        Integer chatPetId = null;
                        ChatPet parentChatPet = null;
                        //如果父亲是族群创始宠物
                        if (ethnicGroupsService.isFounderEventKey(parentIdStr)) {

                            Integer ethnicGroupsId = ethnicGroupsService.createSecondEthnicGroups(wxPubOriginId, wxFanOpenId);

                            EthnicGroups ethnicGroups = ethnicGroupsService.getFounderEthnicGroups(wxPubOriginId);

                            chatPetId = chatPetService.generateChatPet(wxPubOriginId, wxFanOpenId, ethnicGroups.getId(), ethnicGroupsId ,null);
                        } else {
                            Integer parentId = Integer.valueOf(parentIdStr);

                            parentChatPet = chatPetService.getById(parentId);

                            //如果不是长老
                            if (parentChatPet == null) {
                                String replyContent = "链接有误，请检查链接参数";

                                return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
                            }

                            Integer secondEthnicGroupsId = parentChatPet.getSecondEthnicGroupsId();
                            Integer ethnicGroupsId = parentChatPet.getEthnicGroupsId();
                            chatPetId = chatPetService.generateChatPet(wxPubOriginId, wxFanOpenId, ethnicGroupsId, secondEthnicGroupsId ,parentId);
                        }

                        String parentWxFanNickname = null;
                        if(parentChatPet != null){
                            String parentWxFanOpenId = parentChatPet.getWxFanOpenId();
                            WxFan parentWxFan = wxFanService.getWxFan(wxPubOriginId, parentWxFanOpenId);
                            parentWxFanNickname = parentWxFan.getNickname();
                        }else {
                            parentWxFanNickname = "喵小咪";
                        }

                        CustomerNewsItem customerNewsItem = new CustomerNewsItem();
                        /*String description = wxFan.getNickname() + "的斑马，出生于2018年4月12日8点30分您已经成功接受# " + parentWxFanNickname + "#的邀请，创造了一只独一无二的斑马，\n" +
                                "加入了斑马星球。\n" +
                                "\n" +
                                "斑马星球的基本规律：多爱心互动，多真诚聊天\n" +
                                "等到长大，虚拟宠物带回现实Money，报效宠爸宠妈\n" +
                                "\n" +
                                "聊天中会随机触发事件\n" +
                                "每一次点击链接，完成每日任务，即获得成长经验值";*/

                        Calendar calendar = Calendar.getInstance();

                        ChatPet myChatPet = chatPetService.getById(chatPetId);
                        Date date = myChatPet.getCreateTime();
                        calendar.setTime(date);
                        String description = "尊贵的" + wxFan.getNickname() + "铲屎官，您已经成功接受 " + parentWxFanNickname  + " 的邀请，加入了喵小咪星球。\n" +
                                "自此历史浓重的记录了一笔：#" + wxFan.getNickname() + "#的喵小咪，出生于" + (calendar.get(Calendar.YEAR)) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH)+ "日" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分。\n" +
                                " \n" +
                                "现在，可以在下面聊天栏里跟我说第一句话。\n" +
                                "喵~期待~";
                        customerNewsItem.setDescription(description);

                        //微信网页授权url参数拼接
                        Integer wxPubIdParam = this.createChatPetH5Param(wxPubOriginId);

                        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
                        //String uri = "/res/wedo/zebra.html?id=" + chatPetId;
                        String uri = "/api/wx/oauth/redirect?id="+wxPubIdParam;
                        String url = domain + uri;
                        //url = url.replace("http://", "https://");

                        customerNewsItem.setUrl(url);
                        customerNewsItem.setPicUrl(chatPetService.getNewsMessageCoverUrl());

                        String replyContent = "喵！World!";
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

    /**
     * 微信h5授权url参数拼接  ?wxPubId = ?
     * @param wxPubOriginId
     * @return
     */
    private Integer createChatPetH5Param(String wxPubOriginId){
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
        return wxPub.getId();
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

    /**
     * 回复单一图文消息
     * @param wxPubOriginId
     * @param wxFanOpendId
     * @param customerNewsItem
     * @return
     */
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
