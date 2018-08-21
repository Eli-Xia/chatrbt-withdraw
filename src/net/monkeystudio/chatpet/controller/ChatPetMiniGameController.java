package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.minigame.QueryMiniGameReq;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.MiniGameTag;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.MiniGameTagService;
import net.monkeystudio.chatrbtw.service.WxMiniGameService;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.MiniGameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/chat-pet/mini-game")
public class ChatPetMiniGameController extends ChatPetBaseController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private WxMiniGameService wxMiniGameService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private MiniGameTagService miniGameTagService;


    @ResponseBody
    @RequestMapping(value = "/handpicked", method = RequestMethod.POST)
    public RespBase getHandpickedMiniGameVOList(@RequestBody QueryMiniGameReq queryMiniGameReq) {
        Integer fanId = getUserId();

        Integer startIndex = queryMiniGameReq.getStartIndex();

        Integer pageSize = queryMiniGameReq.getPageSize();

        ChatPet chatPet = chatPetService.getByWxFanId(fanId);

        List<MiniGameVO> vos = wxMiniGameService.getHandpickedMinigameListVOByPage(startIndex, pageSize, chatPet.getId());

        return respHelper.ok(vos);
    }

    @ResponseBody
    @RequestMapping(value = "/by-tag", method = RequestMethod.POST)
    public RespBase getClassifiedMiniGameVOList(@RequestBody QueryMiniGameReq queryMiniGameReq) {
        Integer fanId = getUserId();

        Integer startIndex = queryMiniGameReq.getStartIndex();

        Integer pageSize = queryMiniGameReq.getPageSize();

        Integer tagId = queryMiniGameReq.getTagId();

        ChatPet chatPet = chatPetService.getByWxFanId(fanId);

        List<MiniGameVO> vos = wxMiniGameService.getClassifiedMinigameListVOByPage(startIndex, pageSize, tagId, chatPet.getId());

        return respHelper.ok(vos);
    }

    @ResponseBody
    @RequestMapping(value = "/tags", method = RequestMethod.POST)
    public RespBase getMinigameTags(HttpServletRequest request) {
        Integer fanId = getUserId();

        List<MiniGameTag> miniGameTagList = miniGameTagService.getMiniGameTagList();

        return respHelper.ok(miniGameTagList);
    }
}
