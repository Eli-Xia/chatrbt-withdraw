package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPetMission;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.ChatPetMissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by bint on 2018/4/20.
 */
@Service
public class ChatPetMissionService {

    public final static Integer CHAT_PET_MISSION_TYPE_FIXED = 0;

    public final static Integer CHAT_PET_MISSION_TYPE_RANDOM = 1;

    @Autowired
    private ChatPetMissionMapper chatPetMissionMapper;



    /**
     * 后台新增任务类型
     * @param chatPetMission
     */
    public void save(ChatPetMission chatPetMission){
        ChatPetMission cpm = new ChatPetMission();

        cpm.setCoin(chatPetMission.getCoin());
        cpm.setIsActive(chatPetMission.getIsActive());
        cpm.setMissionCode(chatPetMission.getMissionCode());
        cpm.setMissionName(chatPetMission.getMissionName());

        chatPetMissionMapper.insert(cpm);
    }

    /**
     * 更新任务类型
     */
    public void update(ChatPetMission chatPetMission){
        chatPetMissionMapper.updateByPrimaryKey(chatPetMission);
    }

    /**
     * 根据任务code获取任务对象
     * @param missionCode
     * @return
     */
    public ChatPetMission getByMissionCode(Integer missionCode){
        return chatPetMissionMapper.selectByMissionCode(missionCode);
    }

    public ChatPetMission getById(Integer id){
        return chatPetMissionMapper.selectByPrimaryKey(id);
    }

    public List<ChatPetMission> getActiveMissions(){
        return chatPetMissionMapper.selectActiveMissionList();
    }




}
