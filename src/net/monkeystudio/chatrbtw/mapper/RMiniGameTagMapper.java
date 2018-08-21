package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.MiniGameTag;
import net.monkeystudio.chatrbtw.entity.RMiniGameTag;
import net.monkeystudio.chatrbtw.mapper.bean.minigame.MiniGameIdsQueryObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RMiniGameTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RMiniGameTag record);

    RMiniGameTag selectByPrimaryKey(Integer id);

    List<RMiniGameTag> selectAll();

    int updateByPrimaryKey(RMiniGameTag record);

    Integer deleteTagsByMiniGameId(Integer miniGameId);

    Integer batchInsert(List<RMiniGameTag> list);

    List<Integer> selectTagListByMiniGameId(Integer miniGameId);

    List<Integer> selectMiniGameIdList(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize, @Param("tagId") Integer tagId);
}