package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.Ad;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by bint on 2017/12/8.
 */
public interface AdMapper {

    Ad selectById(Integer id);

    Ad selectNewestByType(@Param("adType") Integer adType,@Param("pushType") Integer pushType);

    List<Ad> selectByPage(@Param("startIndex") Integer startIndex,@Param("pageSize") Integer pageSize);

    Integer count();

    Integer update(Ad ad);

    Integer insert(Ad ad);

    Integer updateByPrimaryKeySelective(Ad ad);

    Integer detete(Integer id);

    List<Ad> selectAll();

}
