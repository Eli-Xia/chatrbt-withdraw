package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatRobotBaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 03/01/2018.
 */
public interface ChatRobotBaseInfoMapper {
    int insert(ChatRobotBaseInfo chatRobotBaseInfo);

    /**
     * @deprecated 使用 selectListByWxPubOriginId代替
     * @param wxPubOriginId
     * @return
     */
    ChatRobotBaseInfo selectByWxPubOriginId(@Param("wxPubOriginId") String wxPubOriginId);

    int update(ChatRobotBaseInfo chatRobotBaseInfo);

    int deleteByWxPubOriginId(@Param("wxPubOriginId") String wxPubOriginId);

    ChatRobotBaseInfo selectById(Integer id);

    int deleteById(int id);

    List<ChatRobotBaseInfo> selectListByWxPubOriginId(@Param("wxPubOriginId") String wxPubOriginId);
}
