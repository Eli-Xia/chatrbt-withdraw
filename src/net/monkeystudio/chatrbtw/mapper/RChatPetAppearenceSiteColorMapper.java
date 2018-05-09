package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RChatPetAppearenceSiteColor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2018/5/8.
 */
public interface RChatPetAppearenceSiteColorMapper {

    List<RChatPetAppearenceSiteColor> selectByAppearenceSite(@Param("chatPetType") Integer chatPetType ,@Param("chatPetSite") Integer chatPetSite);

}
