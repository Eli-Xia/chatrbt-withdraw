package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxMiniGameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxMiniGame record);

    int insertSelective(WxMiniGame record);

    WxMiniGame selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WxMiniGame record);

    int updateByPrimaryKey(WxMiniGame record);

    List<WxMiniGame> selectAll();

    List<WxMiniGame> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    Integer count();

    Integer delete(Integer id);

    //查询上架且上线的小游戏
    List<WxMiniGame> selectOnlineGameList();

    List<WxMiniGame> selectHandpickedByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    //根据小游戏id集合,获取分页集合
    List<WxMiniGame> selectPageInList(@Param("ids") List<Integer> ids, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);


}