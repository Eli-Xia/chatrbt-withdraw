package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.auctionitem.AuctionRecordAdd;
import net.monkeystudio.chatrbtw.entity.AuctionItem;
import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bint on 2018/6/13.
 */
@RequestMapping("/chat-pet/auction-record")
@Controller
public class ChatPetAuctionRecordController extends ChatPetBaseController{

    @Autowired
    private AuctionRecordService auctionRecordService;

    @Autowired
    private AuctionItemService auctionItemService;

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private RMiniProgramChatPetTypeService rMiniProgramChatPetTypeService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetService chatPetService;

    /**
     * 新增出价记录
     * @param auctionRecordAdd
     * @return
     */
    @RequestMapping(value = "/add" ,method = RequestMethod.POST)
    @ResponseBody
    public RespBase addAuctionRecord(@RequestBody AuctionRecordAdd auctionRecordAdd){

        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.nologin();
        }

        WxFan wxFan = wxFanService.getById(wxFanId);

        Integer auctionItemId = auctionRecordAdd.getAuctionItemId();
        AuctionItem auctionItem = auctionItemService.getById(auctionItemId);

        if(auctionItem == null || auctionItem.getState().intValue() != AuctionItemService.PROCESSING){
            return respHelper.failed("竞拍品有误");
        }


        Integer chatPetType = rMiniProgramChatPetTypeService.getByMiniProgramId(wxFan.getMiniProgramId());
        if(auctionItem.getChatPetType() != chatPetType.intValue()){
            return respHelper.failed("竞拍品的宠物类型有误");
        }

        AuctionRecord auctionRecordFromDB = auctionRecordService.getAuctionRecordByWxFan(wxFanId, auctionItemId);
        if(auctionRecordFromDB != null){
            return respHelper.failed("只能出一次价");
        }

        Float price = auctionRecordAdd.getPrice();
        ChatPet chatPet = chatPetService.getChatPetByWxFanId(wxFanId);
        if(chatPet.getCoin().floatValue() < price.floatValue()){
            return respHelper.failed("出价的价格不能高于所拥有的价格");
        }

        if(price.floatValue()  < 0F){
            return respHelper.failed("出价的价格不能小于0");
        }

        Integer result = auctionRecordService.addAuctionRecord(wxFanId, price, auctionItemId);

        return respHelper.ok(result);
    }

}
