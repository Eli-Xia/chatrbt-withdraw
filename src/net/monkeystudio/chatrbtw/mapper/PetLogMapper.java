package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.PetLog;

public interface PetLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PetLog record);

    int insertSelective(PetLog record);

    PetLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PetLog record);

    int updateByPrimaryKeyWithBLOBs(PetLog record);

    int updateByPrimaryKey(PetLog record);
}