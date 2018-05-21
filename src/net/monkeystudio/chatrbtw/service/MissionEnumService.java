package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
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

    public final static Integer DAILY_CHAT_MISSION_CODE = 2;//每日签到互动任务code
    public final static Integer SEARCH_NEWS_MISSION_CODE = 1;//咨询获取任务code

    private static Map<Integer,ChatPetMission> missionEnumCache = new HashMap<>();

    @PostConstruct
    private void init(){
        List<ChatPetMission> cpms = chatPetMissionService.getActiveMissions();
        for (ChatPetMission cpm : cpms){
            missionEnumCache.put(cpm.getMissionCode(),cpm);
        }
    }

    public ChatPetMission getMissionByCode(Integer code){
        ChatPetMission chatPetMission = missionEnumCache.get(code);

        return chatPetMission;
    }
}
