package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ListUtil;
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
     * @param tagIdList:标签id集合
     * @param miniGameId:小游戏id
     */
    public void saveTagsForMinigame(List<Integer> tagIdList, Integer miniGameId) {
        rMiniGameTagMapper.saveTagsForMiniGame(miniGameId, tagIdList);
    }

    /**
     * 删除小游戏下面的所有标签
     * @param minigameId:小游戏id
     */
    private void deleteByMinigameId(Integer minigameId){
        rMiniGameTagMapper.deleteTagsByMiniGameId(minigameId);
    }

    /**
     * 重新给小游戏分配标签
     * @param tagIdList
     * @param miniGameId
     */
    @Transactional
    public void updateTagsForMinigame(List<Integer> tagIdList, Integer miniGameId){
        this.deleteByMinigameId(miniGameId);

        if(ListUtil.isEmpty(tagIdList)){
            return;
        }

        this.saveTagsForMinigame(tagIdList,miniGameId);
    }

    public List<Integer> getTagListByMiniGameId(Integer miniGameId){
        return rMiniGameTagMapper.selectTagListByMiniGameId(miniGameId);
    }
}
