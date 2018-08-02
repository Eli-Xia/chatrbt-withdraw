package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxMiniGame;

import java.util.List;

public interface WxMiniGameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxMiniGame record);

    int insertSelective(WxMiniGame record);

    WxMiniGame selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WxMiniGame record);

    int updateByPrimaryKey(WxMiniGame record);

    List<WxMiniGame> selectAll();

    Integer delete(Integer id);

    //查询上架且上线的小游戏
    List<WxMiniGame> selectOnlineGameList();

}