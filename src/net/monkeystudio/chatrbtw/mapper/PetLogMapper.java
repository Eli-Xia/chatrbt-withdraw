package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.PetLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PetLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PetLog record);

    int insertSelective(PetLog record);

    PetLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PetLog record);

    int updateByPrimaryKeyWithBLOBs(PetLog record);

    int updateByPrimaryKey(PetLog record);

    List<PetLog> selectDailyPetLog(@Param("chatPetId") Integer chatPetId, @Param("beginTime")Date beginDate, @Param("endTime") Date endDate);

    Float countFanTotalCoin(@Param("wxPubOriginId")String wxPubOriginId, @Param("wxFanOpenId")String wxFanOpenId,@Param("nowTime") Date now);
}