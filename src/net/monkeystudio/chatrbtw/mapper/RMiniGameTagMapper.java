package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RMiniGameTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RMiniGameTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RMiniGameTag record);

    RMiniGameTag selectByPrimaryKey(Integer id);

    List<RMiniGameTag> selectAll();

    int updateByPrimaryKey(RMiniGameTag record);

    Integer deleteTagsByMiniGameId(Integer miniGameId);

    Integer saveTagsForMiniGame(@Param("miniGameId") Integer miniGameId, @Param("tagIds") List<Integer> tagIds);
}