package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxMiniProgram;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxMiniProgramMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxMiniProgram record);

    int insertSelective(WxMiniProgram record);

    WxMiniProgram selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WxMiniProgram record);

    int updateByPrimaryKey(WxMiniProgram record);

    WxMiniProgram selectByAppId(String appId);

    List<WxMiniProgram> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);


}