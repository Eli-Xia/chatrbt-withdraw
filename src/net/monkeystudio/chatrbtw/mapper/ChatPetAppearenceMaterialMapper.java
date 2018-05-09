package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetAppearenceMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2018/5/8.
 */
public interface ChatPetAppearenceMaterialMapper {

    List<ChatPetAppearenceMaterial> selectListBySite(@Param("chatPetType") Integer chatPetType,@Param("site") Integer site);


}
