package net.monkeystudio.chatpet.controller;

import net.monkeystudio.admin.controller.base.ListReqBase;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.MoreLogReq;
import net.monkeystudio.chatrbtw.service.ChatPetLogService;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/chat-pet/log")
public class ChatPetLogController extends ChatPetBaseController{

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private ChatPetLogService chatPetLogService;

    @Autowired
    private ChatPetService chatPetService;

    /**
     * 查看"更多"
     * @param req:分页数据
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/more", method = RequestMethod.POST)
    public RespBase getMoreLog(@RequestBody ListReqBase req){
        Integer fanId = getUserId();

        Integer page = req.getPage();

        Integer pageSize = req.getPageSize();

        Integer startIndex = CommonUtils.page2startIndex(page,pageSize);

        List<PetLogResp> logs = chatPetLogService.getMoreLogs(startIndex, pageSize, fanId);

        return respHelper.ok(logs);
    }

    /**
     * 首页四条日志
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public RespBase getIndexLog(){
        Integer fanId = getUserId();


        return respHelper.ok();
    }
}
