package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxPubTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxPubTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxPubTag record);

    WxPubTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(WxPubTag record);

    List<WxPubTag> selectAll();

    List<WxPubTag> selectTagsByWxPubId(Integer wxPubId);

    List<WxPubTag> selectByPage(@Param("startIndex") Integer startIndex,@Param("pageSize") Integer pageSize);

    Integer count();

}