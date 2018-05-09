package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetAppearenceSite;

import java.util.List;

/**
 * Created by bint on 2018/5/8.
 */
public interface ChatPetAppearenceSiteMapper {

    List<ChatPetAppearenceSite> selectByType(Integer chatPetType);
}
