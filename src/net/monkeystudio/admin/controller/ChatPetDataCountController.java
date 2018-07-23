package net.monkeystudio.admin.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetCountService;
import net.monkeystudio.chatrbtw.service.bean.chatpetcount.YesterdayStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据统计
 * @author xiaxin
 */
@RequestMapping(value = "/admin/chat-pet/data-count")
@Controller
public class ChatPetDataCountController extends BaseController{
    @Autowired
    private RespHelper respHelper;

    @Autowired
    private ChatPetCountService chatPetCountService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase yesterdayCount(){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        YesterdayStatistic yesterdayStatistic = chatPetCountService.getYesterdayStatistic();

        return respHelper.ok(yesterdayStatistic);
    }
}
