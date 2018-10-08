package net.monkeystudio.chatpet.controller;

import net.monkeystudio.admin.controller.req.IdReq;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.BannerAd;
import net.monkeystudio.chatrbtw.service.BannerAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 2018/9/4.
 */
@RequestMapping(value = "/chat-pet/banner-ad")
@Controller
public class ChatPetBannerAdController {

    @Autowired
    private BannerAdService bannerAdService;

    @Autowired
    private RespHelper respHelper;


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getChatPetBackground(){

        List<BannerAd> showBannerAd = bannerAdService.getShowBannerAd();

        return respHelper.ok(showBannerAd);
    }

    @RequestMapping(value = "/click", method = RequestMethod.POST)
    @ResponseBody
    public RespBase click(@RequestBody IdReq idReq){

        bannerAdService.increaseCount(idReq.getId());

        return respHelper.ok();
    }
}
