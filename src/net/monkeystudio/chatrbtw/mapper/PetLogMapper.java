package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.PetLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PetLogMapper {
    int insert(PetLog record);

    int insertSelective(PetLog record);

    PetLog selectByPrimaryKey(Integer id);

    List<PetLog> selectDailyPetLog(@Param("chatPetId") Integer chatPetId, @Param("beginTime")Date beginDate, @Param("endTime") Date endDate);

    int batchInsert(List<PetLog> pls);
}