package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.Bank;

import java.util.List;

public interface BankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bank record);

    int insertSelective(Bank record);

    Bank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bank record);

    int updateByPrimaryKey(Bank record);

    List<Bank> selectAll();

    Bank selectByName(String name);
}