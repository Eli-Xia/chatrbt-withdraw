package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.ethnicgroups.EthnicGroupsRankReq;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetExperinceRankItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 2018/4/28.
 */
@RequestMapping(value = "/chat-pet/ethnic-groups")
@Service
public class EthnicGroupsController {

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private RespHelper respHelper;

    /**
     * 获取二级群群排行
     * @param ethnicGroupsRankReq
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rank", method = RequestMethod.POST)
    public RespBase getSecondEthnicGroups(@RequestBody EthnicGroupsRankReq ethnicGroupsRankReq){

        Integer chatPetId = ethnicGroupsRankReq.getChatPetId();

        Integer pageSize = ethnicGroupsRankReq.getPageSize();

        if(pageSize == null || chatPetId == null){
            return respHelper.failed("参数有误");
        }

        if (pageSize > 10){
            pageSize = 10;
        }

        List<ChatPetExperinceRankItem> list = chatPetService.getChatPetExperinceRankByPet(chatPetId, pageSize);
        return respHelper.ok(list);
    }
}
