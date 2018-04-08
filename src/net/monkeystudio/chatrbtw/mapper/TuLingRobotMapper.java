package net.monkeystudio.chatrbtw.mapper;

import java.util.List;

import net.monkeystudio.chatrbtw.entity.TuLingRobot;

public interface TuLingRobotMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(TuLingRobot record);

    int insertSelective(TuLingRobot record);

    TuLingRobot selectByPrimaryKey(Integer id);
    
    /**
     * 查全表
     * @return
     */
    List<TuLingRobot> selectAll();

    int updateByPrimaryKeySelective(TuLingRobot record);

    int updateByPrimaryKey(TuLingRobot record);
}