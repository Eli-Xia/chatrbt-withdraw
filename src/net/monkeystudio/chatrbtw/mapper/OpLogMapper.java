package net.monkeystudio.chatrbtw.mapper;

import java.util.List;
import java.util.Map;

import net.monkeystudio.chatrbtw.entity.OpLog;

public interface OpLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OpLog record);

    int insertSelective(OpLog record);

    OpLog selectByPrimaryKey(Integer id);
    
    List<OpLog> selectByPage(Map<String,Object> params);
    
    int count(Map<String,Object> params);

    int updateByPrimaryKeySelective(OpLog record);

    int updateByPrimaryKeyWithBLOBs(OpLog record);

    int updateByPrimaryKey(OpLog record);
}