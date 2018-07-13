package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.MiniProgramChatPetService;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.ChatPetCenterStallResp;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.ChatPetGameCenterResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class ChatPetGameCenterService {
    @Autowired
    private WxMiniGameService wxMiniGameService;
    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;
    @Autowired
    private MiniProgramChatPetService miniProgramChatPetService;
    @Autowired
    private WxFanService wxFanService;
    @Autowired
    private ChatPetService chatPetService;

    private final static Integer CENTER_STALL_STATE_NOT_FINISH = 0;//摊位状态 未完成
    private final static Integer CENTER_STALL_STATE_FINISH = 1;//摊位状态 已完成

    /**
     * 根据fanid获取游戏中心信息
     * @param fanId
     * @return
     */
    public ChatPetGameCenterResp getGameCenterResp(Integer fanId){

        ChatPetGameCenterResp resp = new ChatPetGameCenterResp();

        ChatPet chatPet = chatPetService.getByWxFanId(fanId);
        Integer chatPetId = chatPet.getId();

        List<ChatPetCenterStallResp> chatPetCenterStallInfoList = this.getChatPetCenterStallInfoList(chatPetId);
        resp.setChatPetCenterStallList(chatPetCenterStallInfoList);

        List<WxMiniGame> miniGameInfoList = this.getMiniGameInfoList();
        resp.setMiniGameList(miniGameInfoList);

        return resp;
    }

    /**
     * 获取猫市中心摊位信息
     * @return
     */
    private List<ChatPetCenterStallResp> getChatPetCenterStallInfoList(Integer chatPetId){

        List<ChatPetCenterStallResp> list = new ArrayList<>();
        //赠送一只猫六六
        ChatPetPersonalMission param = new ChatPetPersonalMission();
        param.setChatPetId(chatPetId);
        param.setCreateTime(DateUtils.getBeginDate(new Date()));
        param.setMissionCode(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE);
        param.setState(MissionStateEnum.GOING_ON.getCode());

        List<ChatPetPersonalMission> personalMissionList = chatPetMissionPoolService.getPersonalMissionListByParam(param);

        ChatPetCenterStallResp inviteStall = new ChatPetCenterStallResp();
        inviteStall.setTitle("赠送一只猫六六");
        inviteStall.setDescription("每日一送");
        if(ListUtil.isEmpty(personalMissionList)){
            inviteStall.setState(CENTER_STALL_STATE_NOT_FINISH);
        }else{
            inviteStall.setState(CENTER_STALL_STATE_FINISH);
        }
        list.add(inviteStall);

        //每日登录
        ChatPetCenterStallResp dailyLoginStall = new ChatPetCenterStallResp();
        dailyLoginStall.setState(CENTER_STALL_STATE_FINISH);//用户进入到市中心每日登录一定是已完成
        dailyLoginStall.setTitle("每日登录");
        dailyLoginStall.setDescription("+1 经验值");
        list.add(dailyLoginStall);

        //拍卖大堂
        ChatPetCenterStallResp autionHallInstall = new ChatPetCenterStallResp();
        autionHallInstall.setTitle("拍卖大堂");
        autionHallInstall.setDescription("用猫饼参与拍卖");
        autionHallInstall.setState(null);
        list.add(autionHallInstall);

        //公众号打招呼
        ChatPetCenterStallResp wxPubSayHiInstall = new ChatPetCenterStallResp();
        wxPubSayHiInstall.setTitle("公众号打招呼");
        wxPubSayHiInstall.setDescription("+1 经验值");
        wxPubSayHiInstall.setState(CENTER_STALL_STATE_NOT_FINISH);
        list.add(wxPubSayHiInstall);

        //小论坛
        ChatPetCenterStallResp smallShopInstall = new ChatPetCenterStallResp();
        smallShopInstall.setTitle("小论坛");
        smallShopInstall.setDescription("体验论坛获经验值");
        smallShopInstall.setState(null);
        list.add(smallShopInstall);

        //小电商
        ChatPetCenterStallResp smallBbcInstall = new ChatPetCenterStallResp();
        smallBbcInstall.setTitle("公众号打招呼");
        smallBbcInstall.setDescription("逛电商获经验值");
        smallBbcInstall.setState(null);
        list.add(wxPubSayHiInstall);

        return list;
    }

    private List<WxMiniGame> getMiniGameInfoList(){
        return wxMiniGameService.getWxMiniGameList();
    }
}
