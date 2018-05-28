package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AdPushLog;
import net.monkeystudio.chatrbtw.mapper.bean.adpushLog.AdPushLogDailyCount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2017/12/1.
 */
public interface AdPushLogMapper {

    Integer insert(AdPushLog adPushLog);

    List<AdPushLogDailyCount> countDaily(@Param("startTime") Long startTime ,@Param("endTime") Long enTime ,@Param("adId") Integer adId);

    AdPushLog selectByPrimaryKey(Integer id);

    Integer countAdPushCount4WxFan(@Param("wxPubAppId")String wxPubAppId,@Param("wxFanOpenId")String wxFanOpenId,@Param("adId")Integer adId);
}
