package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.auctionitem.AuctionRecordAdd;
import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.service.AuctionRecordService;
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
    private RespHelper respHelper;

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

        Float price = auctionRecordAdd.getPrice();
        Integer auctionItemId = auctionRecordAdd.getAuctionItemId();

        AuctionRecord auctionRecordFromDB = auctionRecordService.getAuctionRecordByWxFan(wxFanId, auctionItemId);
        if(auctionRecordFromDB != null){
            return respHelper.failed("只能出一次价");
        }

        Integer result = auctionRecordService.addAuctionRecord(wxFanId, price, auctionItemId);

        return respHelper.ok(result);
    }

}
