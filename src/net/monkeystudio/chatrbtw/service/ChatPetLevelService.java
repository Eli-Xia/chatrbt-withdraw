package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetLevel;
import net.monkeystudio.chatrbtw.mapper.ChatPetLevelMapper;
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

    public static void main(String[] args) {
        ChatPetLevelService chatPetLevelService = new ChatPetLevelService();

        System.out.println(chatPetLevelService.calculateLevel(40));
    }

}
