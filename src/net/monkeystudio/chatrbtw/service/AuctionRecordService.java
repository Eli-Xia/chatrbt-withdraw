package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.mapper.AuctionRecordMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetlog.SaveChatPetLogParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/6/11.
 */
@Service
public class AuctionRecordService {

    @Autowired
    private AuctionRecordMapper auctionRecordMapper;

    @Autowired
    private ChatPetLogService chatPetLogService;

    /**
     * 获取最高出价的记录
     * @param auctionItemId
     * @return
     */
    public AuctionRecord getMaxPriceAuctionItem(Integer auctionItemId){
        return auctionRecordMapper.selectByAuctionItemId(auctionItemId );
    }


    public List<AuctionRecord> getMaxPriceAuctionItemList(Integer auctionItemId){

        return auctionRecordMapper.selectListByAuctionItemId(auctionItemId ,100);

    }

    /**
     *
     * @param auctionRecord
     * @return
     */
    private Integer save(AuctionRecord auctionRecord){
        return auctionRecordMapper.insert(auctionRecord);
    }

    /**
     * 获得该拍卖品有多少人参与
     * @param id
     * @return
     */
    public Integer countParticipant(Integer id) {

        Integer result = auctionRecordMapper.countParticipant(id);

        if(result == null){
            return 0;
        }

        return result;
    }

    /**
     * 得到个人出价最高的记录
     * @param wxFanId
     * @return
     */
    public AuctionRecord getAuctionRecordByWxFan(Integer wxFanId ,Integer auctionItemId){
        return auctionRecordMapper.selectMaxByWxFanId(wxFanId, auctionItemId);
    }


    public Integer addAuctionRecord(Integer wxFanId ,Float price ,Integer auctionItemId){

        AuctionRecord auctionRecord = new AuctionRecord();

        auctionRecord.setBidTime(new Date());
        auctionRecord.setAuctionItemId(auctionItemId);
        auctionRecord.setWxFanId(wxFanId);
        auctionRecord.setPrice(price);
        Integer recordId = this.save(auctionRecord);

        //宠物竞拍日志
        SaveChatPetLogParam param = new SaveChatPetLogParam();


        //param.setChatPetId();
        param.setChatPetLogType(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_MISSION_REWARD);
        chatPetLogService.saveChatPetDynamic(param);

        return recordId;
    }


}
