package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.Province;

import java.util.List;
import java.util.Map;

public interface ProvinceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);

    List<Province> selectAll();

    Province selectByParamMap(Map<String,Object> map);

    Province selectByName(String name);
}