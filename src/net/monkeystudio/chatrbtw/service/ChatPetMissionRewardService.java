package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.service.bean.chatpetmission.MissionReward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author xiaxin
 */
@Service
public class ChatPetMissionRewardService {
    @Autowired
    private ChatPetMissionEnumService chatPetMissionEnumService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    /**
     * 获取资讯阅读任务奖励
     * @return
     */
    public MissionReward getSearchNewsMissionReward(){
        MissionReward mr = new MissionReward();

        Float searchNewMissionRandomCoin = this.getSearchNewMissionRandomCoin();
        mr.setCoin(searchNewMissionRandomCoin);

        Float searchNewMissionRandomExperience = this.getSearchNewMissionRandomExperience();
        mr.setExperience(searchNewMissionRandomExperience);

        return mr;
    }


    /**
     * 获取赠送猫六六任务奖励
     * @return
     */
    public MissionReward getPresentCatMissionReward(Integer chatPetId){
        MissionReward mr = new MissionReward();

        BigDecimal ethnicGroupsAdditionRadio = ethnicGroupsService.getEthnicGroupsAdditionRadio(chatPetId);

        Float experience = chatPetMissionEnumService.getMissionByCode(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE).getExperience();
        BigDecimal bd = ethnicGroupsAdditionRadio.multiply(new BigDecimal(experience));
        mr.setExperience(bd.floatValue());

        mr.setCoin(0F);

        return mr;
    }

    /**
     * 获取玩NEW小游戏任务奖励
     * @return
     */
    public MissionReward getPlayNewMiniGameMissionReward(Integer chatPetId){
        MissionReward mr = new MissionReward();

        BigDecimal ethnicGroupsAdditionRadio = ethnicGroupsService.getEthnicGroupsAdditionRadio(chatPetId);

        Float playNewMiniGameExperience = this.getPlayNewMiniGameExperience();
        BigDecimal bd = ethnicGroupsAdditionRadio.multiply(new BigDecimal(playNewMiniGameExperience));
        mr.setExperience(bd.floatValue());

        mr.setCoin(0F);

        return mr;
    }

    /**
     * 获取玩非NEW小游戏任务奖励
     * @return
     */
    public MissionReward getPlayOldMiniGameMissionReward(Integer chatPetId){
        MissionReward mr = new MissionReward();

        BigDecimal ethnicGroupsAdditionRadio = ethnicGroupsService.getEthnicGroupsAdditionRadio(chatPetId);

        Float playOldMiniGameExperience = this.getPlayOldMiniGameExperience();
        BigDecimal bd = ethnicGroupsAdditionRadio.multiply(new BigDecimal(playOldMiniGameExperience));
        mr.setExperience(bd.floatValue());

        mr.setCoin(0F);

        return mr;
    }

    /**
     * 获取小程序每日登录任务奖励
     * @return
     */
    public MissionReward getLoginMiniProgramMisisonReward(Integer chatPetId){
        MissionReward mr = new MissionReward();

        BigDecimal ethnicGroupsAdditionRadio = ethnicGroupsService.getEthnicGroupsAdditionRadio(chatPetId);
        BigDecimal bd = ethnicGroupsAdditionRadio.multiply(new BigDecimal(1));

        mr.setExperience(bd.floatValue());
        mr.setCoin(0F);

        return mr;
    }

    /**
     * 获取公众号每日聊天互动签到任务奖励
     * @return
     */
    public MissionReward getDailyChatMissionReward(){
        MissionReward mr = new MissionReward();

        Float coin = chatPetMissionEnumService.getMissionByCode(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE).getCoin();
        Float experience = chatPetMissionEnumService.getMissionByCode(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE).getExperience();

        mr.setCoin(coin);
        mr.setExperience(experience);

        return mr;
    }


    //获取资讯任务随机奖励经验值  1.5 ~ 2.5
    private Float getSearchNewMissionRandomExperience(){
        Random random = new Random();
        Float f = ( random.nextInt(10) + 15 ) / 10F;
        return f;
    }

    //获取资讯任务随机奖励金币值  0.38 ~ 0.63
    private Float getSearchNewMissionRandomCoin(){
        // ( 0 ~ 25 + 38 ) / 100
        Random random = new Random();
        int i = random.nextInt(25) + 38;
        Float f = i / 100F;
        return f;
    }

    /**
     * 获取NEW小游戏经验值
     * 1.0 ~ 2.0随机
     * @return
     */
    private Float getPlayNewMiniGameExperience(){
        Random random = new Random();
        Float f = ( random.nextInt(100) + 100 ) / 100F;
        return f;
    }

    /**
     * 获取没有NEW小游戏经验值
     * @return
     */
    private Float getPlayOldMiniGameExperience(){
        Float f = 0.2F;
        return f;
    }
}
