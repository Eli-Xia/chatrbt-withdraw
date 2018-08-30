package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AccountFlow;
import net.monkeystudio.chatrbtw.entity.Withdraw;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bint on 2018/8/13.
 */
public interface WithdrawMapper {
    Integer insert(Withdraw record);

    Integer update(Withdraw record);

    Withdraw selectByPrimaryKey(Integer id);

    BigDecimal sumByWxFanIdAndDate(@Param("wxFanId") Integer wxFanId, @Param("beginTime") Date beginTime);

    Integer countByWxFanIdAndDate(@Param("wxFanId") Integer wxFanId, @Param("beginTime") Date beginTime);

    Date selectLastPayTime(@Param("wxFanId") Integer wxFanId, @Param("beginTime") Date beginTime);

}
