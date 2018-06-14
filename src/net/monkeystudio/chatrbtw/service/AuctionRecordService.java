package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.mapper.AuctionRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by bint on 2018/6/11.
 */
@Service
public class AuctionRecordService {

    @Autowired
    private AuctionRecordMapper auctionRecordMapper;

    /**
     * 获取最高出价的记录
     * @param auctionItemId
     * @return
     */
    public AuctionRecord getMaxPriceAuctionItem(Integer auctionItemId){
        return auctionRecordMapper.selectByAuctionItemId(auctionItemId );
    }

    private Integer insert(AuctionRecord auctionRecord){
        return auctionRecordMapper.insert(auctionRecord);
    }

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

        return auctionRecordMapper.insert(auctionRecord);
    }
}
