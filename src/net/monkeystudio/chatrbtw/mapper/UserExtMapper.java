package net.monkeystudio.chatrbtw.mapper;

import java.util.Map;

import net.monkeystudio.chatrbtw.entity.UserExt;

public interface UserExtMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserExt record);

    int insertSelective(UserExt record);

    UserExt selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserExt record);
    
    int updateByPrimaryKeySelectiveWithMapParams(Map<String,Object> params);

    int updateByPrimaryKey(UserExt record);
}