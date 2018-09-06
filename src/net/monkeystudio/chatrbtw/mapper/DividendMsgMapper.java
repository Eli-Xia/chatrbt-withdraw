package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.DividendMsg;

import java.util.List;

public interface DividendMsgMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DividendMsg record);

    DividendMsg selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(DividendMsg record);

    List<DividendMsg> selectAll();
}