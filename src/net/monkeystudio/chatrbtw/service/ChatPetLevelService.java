package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.ChatPetLevel;
import net.monkeystudio.chatrbtw.mapper.ChatPetLevelMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Integer calculateLevel(Float experience){
        List<ChatPetLevel> allLevel = this.getAscAllLevel();

        int level = 0;

        Float remaining = experience;

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
    /*public ExperienceProgressRate getProgressRate(Integer experience){

        int remaining = experience;

        List<ChatPetLevel> allLevel = this.getAscAllLevel();
        Integer lastRemainingExperience = 0;

        Integer lastLevelExperience = allLevel.get(0).getExperience();

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
    }*/

    public ExperienceProgressRate getProgressRate(Float experience){
        //当前等级
        Integer nowLevel = this.calculateLevel(experience);

        //当前等级的最低经验值
        Integer nowLevelExperience = this.lowestExperienceOfLevel(nowLevel);

        //当前分数超出最低分几分
        Float overExperience = experience - nowLevelExperience;

        //下一级
        Integer nextLevel = nowLevel + 1;

        //下一级所需经验值
        Float updateNeedExperience = Float.parseFloat(this.upgradeNeedExperience(nextLevel).toString());

        ExperienceProgressRate experienceProgressRate = new ExperienceProgressRate();

        experienceProgressRate.setOwn(overExperience);
        experienceProgressRate.setNeed(updateNeedExperience);

        return experienceProgressRate;
    }

    /**
     * 从下一级升到当前等级所需经验值
     * @param level
     * @return
     */
    private Integer upgradeNeedExperience(Integer level){
        List<ChatPetLevel> allLevel = this.getAscAllLevel();
        for (ChatPetLevel cpl : allLevel){
            if(cpl.getLevel() == level){
                return cpl.getExperience();
            }
        }
        return null;
    }

    /**
     * 到达该等级所需总经验值
     * @param level
     * @return
     */
    private Integer lowestExperienceOfLevel(Integer level){

        Integer experience = 0;

        List<ChatPetLevel> allLevel = this.getAscAllLevel();

        for(ChatPetLevel cpl : allLevel){
            if(cpl.getLevel() > level){
                break;
            }
            experience += cpl.getExperience();
        }

        return experience;
    }

    /**
     * 判断完成任务增加经验后是否会升级
     * @return
     */
    public boolean isUpgrade(Float oldExperience,Float newExperience){
        Integer oldLevel = this.calculateLevel(oldExperience);

        Integer newLevel = this.calculateLevel(newExperience);

        return oldLevel.intValue() == newLevel.intValue() - 1;
    }

}
