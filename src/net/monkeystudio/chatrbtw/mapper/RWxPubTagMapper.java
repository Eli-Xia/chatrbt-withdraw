package net.monkeystudio.chatrbtw.mapper;

import java.util.List;
import net.monkeystudio.chatrbtw.entity.RWxPubTag;
import org.apache.ibatis.annotations.Param;

public interface RWxPubTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RWxPubTag record);

    RWxPubTag selectByPrimaryKey(Integer id);

    List<RWxPubTag> selectAll();

    int updateByPrimaryKey(RWxPubTag record);

    Integer deleteTagsByWxPubId(Integer wxPubId);

    Integer saveTagsForWxPub(@Param("wxPubId")Integer wxPubId, @Param("tagIds")List<Integer> tagIds);
}