package net.monkeystudio.chatrbtw.service;

import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class ChatPetLogTypeService {

    public final static Integer CHAT_PET_LOG_TYPE_BORN = 1;//出生日志

    public final static Integer CHAT_PET_LOG_TYPE_LEVEL_REWARD = 2;//等级奖励日志

    public final static Integer CHAT_PET_LOG_TYPE_JOIN_AUTION = 3; //参与竞标日志

    public final static Integer CHAT_PET_LOG_TYPE_AUTION_SUCCESS = 4;//竞标成功日志

    public final static Integer CHAT_PET_LOG_TYPE_MISSION_REWARD = 5;//任务日志

}
