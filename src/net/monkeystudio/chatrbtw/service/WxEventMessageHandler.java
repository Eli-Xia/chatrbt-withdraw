package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.EthnicGroups;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.sdk.wx.bean.SubscribeEvent;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.mp.aes.XMLParse;
import net.monkeystudio.wx.service.WxPubService;
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
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatPetRewardService chatPetRewardService;



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

                        if(!ethnicGroupsService.allowToAdopt(wxPubOriginId)){
                            String replyContent = "今日宠物已经领完了，明天早点来哟！";
                            return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
                        }

                        String parentIdStr = null;
                        if(qrSceneStr.indexOf("qrscene_" + EthnicGroupsService.EVENT_SPECIAL_STR) != -1){
                            parentIdStr = qrSceneStr.replace("qrscene_" + EthnicGroupsService.EVENT_SPECIAL_STR, "");
                        }else {
                            if(qrSceneStr.indexOf(EthnicGroupsService.EVENT_SPECIAL_STR) != -1){
                                parentIdStr = qrSceneStr.replace(EthnicGroupsService.EVENT_SPECIAL_STR, "");
                            }
                        }
                        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId,wxFanOpenId);

                        Integer chatPetId = null;
                        ChatPet parentChatPet = null;
                        //如果父亲是族群创始宠物
                        if (ethnicGroupsService.isFounderEventKey(parentIdStr)) {

                            Integer ethnicGroupsId = ethnicGroupsService.createSecondEthnicGroups(wxPubOriginId, wxFanOpenId);

                            EthnicGroups ethnicGroups = ethnicGroupsService.getFounderEthnicGroups(wxPubOriginId);

                            chatPetId = chatPetService.generateChatPet(wxPubOriginId, wxFanOpenId, ethnicGroups.getId(), ethnicGroupsId ,null);
                        } else {
                            Integer parentId = Integer.valueOf(parentIdStr);

                            Log.d("============ 父亲宠物的id = {?} =============",parentIdStr);

                            parentChatPet = chatPetService.getById(parentId);

                            //如果不是长老
                            if (parentChatPet == null) {
                                String replyContent = "链接有误，请检查链接参数";

                                return this.replyTextStr(wxPubOriginId, wxFanOpenId, replyContent);
                            }

                            Integer secondEthnicGroupsId = parentChatPet.getSecondEthnicGroupsId();
                            Integer ethnicGroupsId = parentChatPet.getEthnicGroupsId();
                            chatPetId = chatPetService.generateChatPet(wxPubOriginId, wxFanOpenId, ethnicGroupsId, secondEthnicGroupsId ,parentId);
                            Log.d("================== 孩子宠物的id = {?} ===============",chatPetId.toString());


                            ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getDailyPersonalMission(parentId,ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE);

                            if(chatPetPersonalMission != null){
                                //生成任务奖励
                                chatPetRewardService.saveRewardItemWhenMissionDone(parentId,chatPetPersonalMission.getId());
                                //更新任务完成状态,设置被邀请人
                                chatPetMissionPoolService.updateMissionWhenInvited(chatPetPersonalMission.getId(),wxFan.getId());
                            }

                        }

                        CustomerNewsItem customerNewsItem = chatPetService.getChatNewsItem(chatPetId);

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
