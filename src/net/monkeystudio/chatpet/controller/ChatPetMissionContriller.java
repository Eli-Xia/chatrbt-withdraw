package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetMissionPoolService;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.CompleteMissionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bint on 2018/7/11.
 */
@Controller
@RequestMapping(value = "/chat-pet/personal-mission")
public class ChatPetMissionContriller extends BaseController{

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private RespHelper respHelper;


    /**
     * TODO 校验该任务是不是当前用粉丝的
     * 完成玩小游戏接口
     * @param personalMissionId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/finish-mission", method = RequestMethod.POST)
    public RespBase finishDailyMiniGame(@RequestParam Integer personalMissionId){

        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.nologin();
        }

        CompleteMissionParam completeMissionParam = new CompleteMissionParam();
        completeMissionParam.setPersonalMissionId(personalMissionId);

        chatPetMissionPoolService.completeChatPetMission(completeMissionParam);

        return respHelper.ok();
    }

}
