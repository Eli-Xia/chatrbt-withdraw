package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2018/4/16.
 */
public interface ChatPetMapper {

    Integer insert(ChatPet chatPet);

    ChatPet selectById(Integer id);

    ChatPet selectByParam(ChatPet chatPet);

    Integer increaseExperience(@Param("id") Integer chatPetId ,@Param("augend") Float augend);

    Integer increaseCoin(@Param("id") Integer chatPetId ,@Param("rewardCoin") Float rewardCoin);

    List<ChatPet> selectListByExperience(@Param("secondEthnicGroupsId") Integer secondEthnicGroupsId, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    Integer countSecondEthnicGroupsById(@Param("secondEthnicGroupsId") Integer secondEthnicGroupsId);

    Integer countByAppearceCode(@Param("appearenceCode") String appearenceCode);
}
