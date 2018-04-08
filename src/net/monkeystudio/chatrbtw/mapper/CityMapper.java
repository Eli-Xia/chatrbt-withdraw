package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.City;

import java.util.List;
import java.util.Map;

public interface CityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(City record);

    int insertSelective(City record);

    City selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(City record);

    int updateByPrimaryKey(City record);

    List<City> selectAll();

    City selectByParamMap(Map<String,Object> map);

    City selectByName(String name);
}