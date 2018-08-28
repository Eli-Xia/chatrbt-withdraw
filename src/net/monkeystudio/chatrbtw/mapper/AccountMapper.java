package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.Account;

/**
 * Created by bint on 2018/8/13.
 */
public interface AccountMapper {
    Integer insert(Account record);

    Account selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKey(Account record);

    Account selectForUpdate(Integer id);
}
