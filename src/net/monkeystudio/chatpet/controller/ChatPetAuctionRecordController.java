package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.service.AuctionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * @param auctionRecord
     * @return
     */
    @RequestMapping("/add")
    public RespBase addAuctionRecord(AuctionRecord auctionRecord){

        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.ok();
        }

        Float price = auctionRecord.getPrice();

        Integer auctionItemId = auctionRecord.getAuctionItemId();

        Integer result = auctionRecordService.addAuctionRecord(wxFanId, price, auctionItemId);

        return respHelper.ok(result);
    }

}
