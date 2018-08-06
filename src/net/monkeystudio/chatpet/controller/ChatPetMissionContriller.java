package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.service.ChatPetMissionEnumService;
import net.monkeystudio.chatrbtw.service.ChatPetMissionPoolService;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetRewardChangeInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.CompleteMissionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by bint on 2018/7/11.
 */
@Controller
@RequestMapping(value = "/chat-pet/personal-mission")
public class ChatPetMissionContriller extends ChatPetBaseController{

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private ChatPetService chatPetService;



    /**
     * TODO 校验该任务是不是当前用粉丝的
     * 完成玩小游戏接口
     * @param wxMiniGameId ;小游戏id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/finish-mission", method = RequestMethod.POST)
    public RespBase finishDailyMiniGame(@RequestParam("id") Integer wxMiniGameId) throws BizException{

        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.nologin();
        }

        if(chatPetMissionPoolService.missionIsFinished(wxFanId,wxMiniGameId)){
            return respHelper.failed("game mission has finished");
        }

        chatPetMissionPoolService.finishDailyMiniGameMission(wxFanId , wxMiniGameId);

        //ChatPetRewardChangeInfo infoAfterReward = chatPetService.getInfoAfterReward(chatPetId);

        return respHelper.ok();
    }

}
