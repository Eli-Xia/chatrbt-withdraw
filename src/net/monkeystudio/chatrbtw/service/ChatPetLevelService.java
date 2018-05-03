package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.ChatPetLevel;
import net.monkeystudio.chatrbtw.mapper.ChatPetLevelMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bint on 2018/4/27.
 */
@Service
public class ChatPetLevelService {

    @Autowired
    ChatPetLevelMapper chatPetLevelMapper;

    private List<ChatPetLevel> getAscAllLevel(){
        return chatPetLevelMapper.selectAscAll();
    }


    /**
     * 计算等级
     * @param experience
     * @return
     */
    public Integer calculateLevel(Integer experience){
        List<ChatPetLevel> allLevel = this.getAscAllLevel();

        int level = 0;

        int remaining = experience;

        for(ChatPetLevel chatPetLevel : allLevel){

            remaining = remaining - chatPetLevel.getExperience();

            if(remaining >= 0){
                level++;
            }else {
                return level;
            }
        }
        return null;
    }


    /**
     * 得到经验值进度
     * @param experience
     * @return
     */
    public ExperienceProgressRate getProgressRate(Integer experience){

        int remaining = experience;

        List<ChatPetLevel> allLevel = this.getAscAllLevel();
        Integer lastRemainingExperience = 0;
        Integer lastLevelExperience = null;

        for(ChatPetLevel chatPetLevel : allLevel){

            remaining = remaining - chatPetLevel.getExperience();

            if(remaining >= 0){
                lastLevelExperience = chatPetLevel.getExperience();
                lastRemainingExperience = remaining;
            }else {
                break;
            }
        }

        ExperienceProgressRate experienceProgressRate = new ExperienceProgressRate();

        experienceProgressRate.setNeed(lastLevelExperience);
        experienceProgressRate.setOwn(lastRemainingExperience);

        return experienceProgressRate;
    }

    /**
     * 判断完成任务增加经验后是否会升级
     * @return
     */
    public boolean isUpgrade(Integer oldExperience,Integer newExperience){
        Integer oldLevel = this.calculateLevel(oldExperience);

        Integer newLevel = this.calculateLevel(newExperience);

        return oldLevel.intValue() == newLevel.intValue() - 1;
    }
}
