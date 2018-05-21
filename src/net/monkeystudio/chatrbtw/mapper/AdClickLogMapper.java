package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AdClickLog;
import net.monkeystudio.chatrbtw.mapper.bean.adclicklog.AdClickLogDailyCount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 24/02/2018.
 */
public interface AdClickLogMapper {

    List<Integer> selectAdId(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("wxPubOriginId") String wxPubOriginId);

    Integer countDayAdClick(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("wxPubOriginId") String wxPubOriginId ,@Param("adId") Integer adId ,@Param("userId") Integer userId);

    List<AdClickLog> selectByPage(@Param("startIndex") Integer startIndex ,@Param("pageSize") Integer pageSize);

    List<AdClickLogDailyCount> countDaily(@Param("startTime") Date startTime, @Param("endTime") Date endTime ,@Param("adId") Integer adId);

    Integer count();

    Integer countTotalByAdId(@Param("adId") Integer adId);

    List<Integer> selectAdIdByUserId(@Param("userId") Integer userId ,@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Integer countTotalAdClick(@Param("userId") Integer userId, @Param("adId")Integer adId);

    Integer countByWxFanAndAd(@Param("wxFanId")Integer wxFanId,@Param("adId")Integer adId);
}
