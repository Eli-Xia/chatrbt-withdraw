package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AccountFlow;
import net.monkeystudio.chatrbtw.entity.Withdraw;

/**
 * Created by bint on 2018/8/13.
 */
public interface WithdrawMapper {
    Integer insert(Withdraw record);
    Integer update(Withdraw record);
    Withdraw selectByPrimaryKey(Integer id);
}
