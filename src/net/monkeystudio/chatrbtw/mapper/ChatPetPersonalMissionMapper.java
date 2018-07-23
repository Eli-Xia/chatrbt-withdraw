package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ChatPetPersonalMissionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatPetPersonalMission record);

    ChatPetPersonalMission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatPetPersonalMission record);

    int updateByPrimaryKey(ChatPetPersonalMission record);

    List<ChatPetPersonalMission> selectListByParam(ChatPetPersonalMission param);

    ChatPetPersonalMission selectByParam(ChatPetPersonalMission param);

    Integer countByChatPetIdAndAdId(@Param("chatPetId")Integer chatPetId,@Param("adId")Integer adId);

    Integer countDispatchMissionAmountByMissionCode(@Param("chatPetId")Integer chatPetId,@Param("missionCode")Integer missionCode,@Param("createTime")Date createTime);

    Long countMiniGameFinishAmount(@Param("miniGameId") Integer miniGameId,@Param("missionCode")Integer missionCode,@Param("state")Integer state);

    Integer countDayPlayGamePeople(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);//根据开始结束时间查询玩游戏人数

    Integer countDayPlayGameTotalAmount(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);//根据开始结束时间查询玩游戏总次数
}