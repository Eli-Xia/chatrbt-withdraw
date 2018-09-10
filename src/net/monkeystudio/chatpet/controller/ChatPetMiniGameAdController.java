package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.MiniGameAd;
import net.monkeystudio.chatrbtw.service.MiniGameAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/chat-pet/mini-game-ad")
public class ChatPetMiniGameAdController extends ChatPetBaseController {
    @Autowired
    private MiniGameAdService miniGameAdService;

    @Autowired
    private RespHelper respHelper;

    /**
     * 游戏广告
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get/{id}", method = RequestMethod.POST)
    public RespBase getIndexLog(@PathVariable("id") Integer id){
        MiniGameAd miniGameAd = miniGameAdService.getById(id);

        return respHelper.ok(miniGameAd);
    }
}
