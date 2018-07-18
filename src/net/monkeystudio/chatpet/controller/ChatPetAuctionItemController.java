package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.auctionitem.QueryAuctionItem;
import net.monkeystudio.chatrbtw.entity.AuctionItem;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.ChatPetAuctionItemListResp;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.ChatPetAuctionItemResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 2018/6/13.
 */
@Controller
@RequestMapping(value = "/chat-pet/auction-item")
public class ChatPetAuctionItemController extends ChatPetBaseController{

    @Autowired
    private AuctionItemService auctionItemService;

    @Autowired
    private RWxPubChatPetTypeService rWxPubChatPetTypeService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private RMiniProgramChatPetTypeService rMiniProgramChatPetTypeService;



    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAuctionItemList(@RequestBody QueryAuctionItem queryAuctionItem) {

        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.nologin();
        }

        Integer page = queryAuctionItem.getPage();
        Integer pageSize = queryAuctionItem.getPageSize();

        if(page == null || pageSize == null){
            return respHelper.failed("参数有误！");
        }

        WxFan wxFan = wxFanService.getById(wxFanId);

        //Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxFan.getWxPubOriginId());

        Integer chatPetType = rMiniProgramChatPetTypeService.getByMiniProgramId(wxFan.getMiniProgramId());

        ChatPetAuctionItemResp chatPetAuctionItemResp = auctionItemService.getAuctionItemListByChatPetType(chatPetType , page , pageSize ,wxFanId);

        return respHelper.ok(chatPetAuctionItemResp);
    }

}
