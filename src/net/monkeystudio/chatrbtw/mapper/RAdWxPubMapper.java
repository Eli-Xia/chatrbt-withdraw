package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RAdWxPub;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RAdWxPubMapper {
    List<RAdWxPub> selectAll();

    int deleteByPrimaryKey(Integer id);

    int insert(RAdWxPub record);

    int insertSelective(RAdWxPub record);

    RAdWxPub selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RAdWxPub record);

    int updateByPrimaryKey(RAdWxPub record);

    Integer countPushAdRecord(Integer adId);

    int deleteByAdId(Integer id);

    Integer batchInsert(List<RAdWxPub> list);

    List<RAdWxPub> selectByAdId(Integer adId);

    Integer updateAdId(@Param("adId") Integer adId,@Param("wxPubId") Integer wxPubId);

    int updateByParamMap(@Param("col") Map<String,Object> columnMap,@Param("con") Map<String,Object> conditionMap);

    List<RAdWxPub> selectByParamMap (Map<String,Object> map);



}