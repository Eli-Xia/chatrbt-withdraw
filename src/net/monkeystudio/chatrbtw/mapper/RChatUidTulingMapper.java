package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RChatUidTuling;

public interface RChatUidTulingMapper {
    int deleteByPrimaryKey(Integer chatUid);

    int insert(RChatUidTuling record);

    int insertSelective(RChatUidTuling record);

    RChatUidTuling selectByPrimaryKey(Integer chatUid);

    int updateByPrimaryKeySelective(RChatUidTuling record);

    int updateByPrimaryKey(RChatUidTuling record);
}