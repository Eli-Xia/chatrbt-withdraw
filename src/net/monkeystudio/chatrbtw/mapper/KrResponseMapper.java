package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.KrResponse;

import java.util.List;

public interface KrResponseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KrResponse record);

    int insertSelective(KrResponse record);

    KrResponse selectByPrimaryKey(Integer id);
    
    List<KrResponse> selectAll();

    int updateByPrimaryKeySelective(KrResponse record);

    int updateByPrimaryKeyWithBLOBs(KrResponse record);

}