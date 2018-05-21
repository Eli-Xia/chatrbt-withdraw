package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPetMission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaxin
 */
@Service
public class MissionEnumService {
    @Autowired
    private ChatPetMissionService chatPetMissionService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    public final static Integer DAILY_CHAT_MISSION_CODE = 2;//每日签到互动任务code
    public final static Integer SEARCH_NEWS_MISSION_CODE = 1;//咨询获取任务code
    public final static Integer INVITE_FRIENDS_MISSION_CODE = 3;//邀请好友code

    @PostConstruct
    private void init(){
        List<ChatPetMission> cpms = chatPetMissionService.getActiveMissions();
        for (ChatPetMission cpm : cpms){
            redisCacheTemplate.setObject(getChatPetMissionEnumCacheKey(cpm.getMissionCode()),cpm);
        }
    }

    /**
     * 根据code获取任务对象
     * @param code
     * @return
     */
    public ChatPetMission getMissionByCode(Integer code){
        ChatPetMission chatPetMission = redisCacheTemplate.getObject(getChatPetMissionEnumCacheKey(code));

        return chatPetMission;
    }

    private String getChatPetMissionEnumCacheKey(Integer missionCode){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + " chatpetmission "+ missionCode.toString();
    }
}
