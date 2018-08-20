package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.RMiniGameTag;
import net.monkeystudio.chatrbtw.mapper.RMiniGameTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RMiniGameTagService {
    @Autowired
    private RMiniGameTagMapper rMiniGameTagMapper;

    /**
     * 给小游戏分配标签
     *
     * @param tagIdList:标签id集合
     * @param miniGameId:小游戏id
     */
    public void saveTagsForMinigame(List<Integer> tagIdList, Integer miniGameId, Boolean handpicked) {
        rMiniGameTagMapper.saveTagsForMiniGame(miniGameId, tagIdList, handpicked);
    }

    /**
     * 删除小游戏下面的所有标签
     *
     * @param minigameId:小游戏id
     */
    private void deleteByMinigameId(Integer minigameId) {
        rMiniGameTagMapper.deleteTagsByMiniGameId(minigameId);
    }

    /**
     * 重新给小游戏分配标签
     *
     * @param tagIdList
     * @param miniGameId
     */
    @Transactional
    public void updateTagsForMinigame(List<Integer> tagIdList, Integer miniGameId, Boolean handpicked) {
        this.deleteByMinigameId(miniGameId);

        if (ListUtil.isEmpty(tagIdList)) {
            return;
        }

        this.saveTagsForMinigame(tagIdList, miniGameId, handpicked);
    }

    public List<Integer> getTagListByMiniGameId(Integer miniGameId) {
        return rMiniGameTagMapper.selectTagListByMiniGameId(miniGameId);
    }

    /**
     * 场景:用于小程序游戏分类
     * 用法:根据标签id和是否为精选编辑查询小游戏id集合
     * @param tagId:标签id
     * @param handpicked:是否为精选编辑
     * @return
     */
    public List<Integer> getMinigameIdsByParam(Integer tagId,Boolean handpicked){
        RMiniGameTag param = new RMiniGameTag();
        param.setTagId(tagId);
        param.setHandpicked(handpicked);
        return rMiniGameTagMapper.selectMiniGameIdsByParam(param);
    }
}
