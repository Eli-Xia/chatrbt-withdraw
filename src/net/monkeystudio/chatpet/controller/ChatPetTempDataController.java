package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.TempDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bint on 2018/8/23.
 */
@RequestMapping(value = "/chat-pet/temp-count")
@Controller
public class ChatPetTempDataController {

    @Autowired
    private TempDataService tempDataService;

    @Autowired
    private RespHelper respHelper;


    @ResponseBody
    @RequestMapping(value = "/click-game" , method = RequestMethod.POST)
    public RespBase clickGame(){

        tempDataService.increaseClickGame();

        return respHelper.ok();
    }


    @ResponseBody
    @RequestMapping(value = "/click-more" , method = RequestMethod.POST)
    public RespBase clickMore(){
        tempDataService.increaseClickMore();

        return respHelper.ok();
    }
}
