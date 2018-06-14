package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/6/11.
 */
public interface AuctionRecordMapper {

    int insert(AuctionRecord auctionRecord);

    AuctionRecord selectByAuctionItemId(@Param("auctionItemId") Integer auctionItemId);

    Integer countParticipant(Integer id);

    AuctionRecord selectMaxByWxFanId(@Param("wxFanId" ) Integer wxFanId ,@Param("auctionItemId") Integer auctionItemId);
}
