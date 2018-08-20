package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.MiniGameTag;
import net.monkeystudio.chatrbtw.entity.WxPubTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MiniGameTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxPubTag record);

    MiniGameTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(WxPubTag record);

    List<MiniGameTag> selectAll();

    List<MiniGameTag> selectTagsByMiniGameId(Integer wxPubId);

    List<MiniGameTag> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    Integer count();

}