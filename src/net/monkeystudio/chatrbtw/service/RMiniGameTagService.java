package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.RMiniGameTag;
import net.monkeystudio.chatrbtw.mapper.RMiniGameTagMapper;
import net.monkeystudio.chatrbtw.mapper.bean.minigame.MiniGameIdsQueryObject;
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
     * 批量插入
     *
     * @param rMiniGameTagList:小游戏与标签关系对象集合
     */
    public void saveTagsForMinigame(List<RMiniGameTag> rMiniGameTagList) {
        rMiniGameTagMapper.batchInsert(rMiniGameTagList);
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
     * @param rMiniGameTagList:小游戏与标签关系对象集合
     * @param miniGameId:小游戏id,用于更新时删除旧关系数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTagsForMinigame(Integer miniGameId, List<RMiniGameTag> rMiniGameTagList) throws BizException {

        this.deleteByMinigameId(miniGameId);

        if (ListUtil.isEmpty(rMiniGameTagList)) {
            throw new BizException("tag list is empty");
        }

        this.saveTagsForMinigame(rMiniGameTagList);
    }

    public List<Integer> getTagListByMiniGameId(Integer miniGameId) {
        return rMiniGameTagMapper.selectTagListByMiniGameId(miniGameId);
    }

    /**
     * 根据标签分类获取上架,上线的小游戏分页数据
     *
     * @return
     */
    public List<Integer> getMiniGameIdListByPage(Integer startIndex, Integer pageSize, Integer tagId) {
        return rMiniGameTagMapper.selectMiniGameIdList(startIndex, pageSize, tagId);
    }
}
