package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RAdWxPubTag;

import java.util.List;
import java.util.Map;

public interface RAdWxPubTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RAdWxPubTag record);

    int insertSelective(RAdWxPubTag record);

    RAdWxPubTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RAdWxPubTag record);

    int updateByPrimaryKey(RAdWxPubTag record);

    int deleteByAdId(Integer adId);

    Integer batchInsert(List<RAdWxPubTag> list);

    List<RAdWxPubTag> selectByAdId(Integer adId);

    List<RAdWxPubTag> selectByParamMap(Map<String,Object> map);
}