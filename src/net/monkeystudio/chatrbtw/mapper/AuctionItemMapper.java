package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AuctionItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/6/8.
 */
public interface AuctionItemMapper {
    int insert(AuctionItem auctionItem);
    List<AuctionItem> selectPageByChatPetType(@Param("startIndex") Integer startIndex ,@Param("pageSize") Integer pageSize , @Param("chatPetType") Integer chatPetType);

    List<AuctionItem> selectPage(@Param("startIndex") Integer startIndex ,@Param("pageSize") Integer pageSize );

    int updateStateAndWxFanId(@Param("id") Integer id , @Param("originIdState") Integer originId , @Param("targetState") Integer targetState , @Param("wxFanId") Integer wxFanId );

    List<AuctionItem> selectByEndTime(@Param("endTime") Date endTime);

    int updateAuctionItem(@Param("startTime") Date startTime ,@Param("endTime") Date endTime ,@Param("id") Integer id ,@Param("chatPetType") Integer chatPetType, @Param("auctionType") Integer auctionType ,@Param("name") String name);

    AuctionItem selectById(Integer id);

    Integer updateShipState(@Param("id") Integer auctionItemId,@Param("shipState") Integer shipState);
}
