package net.monkeystudio.chatrbtw.mapper;


import net.monkeystudio.chatrbtw.entity.Res;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component("rbtwResMapper")
public interface ResMapper {
    int insert(Res record);

    int insertSelective(Res record);

    List<Res> selectAll();

    Integer update(Res role);

    Integer delete(Integer id);

    List<Res> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    Integer count();

    Res selectByPrimaryKey(Integer id);

}