package net.monkeystudio.chatrbtw.mapper;


import net.monkeystudio.chatrbtw.entity.MiniProgram;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MiniProgramMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MiniProgram record);

    int insertSelective(MiniProgram record);

    MiniProgram selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MiniProgram record);

    int updateByPrimaryKey(MiniProgram record);

    MiniProgram selectByAppId(String appId);

    List<MiniProgram> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);


}