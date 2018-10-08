package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.BizAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * Created by bint on 2018/8/13.
 */
public interface BizAccountMapper {
    Integer insert(BizAccount record);

    BizAccount selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKey(BizAccount record);

    BizAccount selectForUpdate(Integer id);

    Integer decrease(@Param("id") Integer id, @Param("amount") BigDecimal amount);

}
