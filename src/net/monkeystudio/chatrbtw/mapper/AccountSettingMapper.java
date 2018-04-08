package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AccountSetting;

public interface AccountSettingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AccountSetting record);

    int insertSelective(AccountSetting record);

    AccountSetting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountSetting record);

    int updateByPrimaryKey(AccountSetting record);

    AccountSetting selectByUserId(Integer userId);
}