package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.minigame.QueryMiniGameReq;
import net.monkeystudio.chatrbtw.service.bean.minigame.QueryMiniGameParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/chat-pet/mini-game")
public class ChatPetMiniGameController extends ChatPetBaseController {
    @Autowired
    private RespHelper respHelper;


    @ResponseBody
    @RequestMapping(value = "/handpicked", method = RequestMethod.POST)
    public RespBase getHandpickedMiniGameVOList(@RequestBody QueryMiniGameReq queryMiniGameReq) {
        Integer fanId = getUserId();

        QueryMiniGameParam param = new QueryMiniGameParam();
        BeanUtils.copyProperties(queryMiniGameReq,param);
        return respHelper.ok();
    }

    @ResponseBody
    @RequestMapping(value = "/by-tag", method = RequestMethod.POST)
    public RespBase getClassifiedMiniGameVOList(@RequestBody QueryMiniGameReq queryMiniGameReq) {
        Integer fanId = getUserId();

        return respHelper.ok();
    }

}
