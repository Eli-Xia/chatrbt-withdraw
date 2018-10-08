package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * Created by bint on 2018/8/13.
 */
public interface AccountMapper {
    Integer insert(Account record);

    Account selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKey(Account record);

    Account selectForUpdate(Integer fanId);

    Integer decrease(@Param("id") Integer id, @Param("amount") BigDecimal amount);
}
