package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.service.bean.chatpetcount.YesterdayStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 招财猫相关数据统计
 * @author xiaxin
 */
@Service
public class ChatPetCountService {

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatPetLoginLogService chatPetLoginLogService;

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    @Autowired
    private ChatPetCoinFlowService chatPetCoinFlowService;

    @Autowired
    private ChatPetExpFlowService chatPetExpFlowService;

    @Autowired
    private ChatPetService chatPetService;

    /**
     * 获取昨日数据统计
     * @return
     */
    public YesterdayStatistic getYesterdayStatistic(){
        YesterdayStatistic yesterdayStatistic = new YesterdayStatistic();
        //总经验值
        //Float totalExp = chatPetExpFlowService.getTotalAmountByYesterday();
        Double totalExp = chatPetService.countTotalExperience(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT);
        yesterdayStatistic.setTotalExperience(totalExp.floatValue());

        //总猫饼数
        //Float totalCoin = chatPetCoinFlowService.getTotalAmountByYesterday();
        Double totalCoin = chatPetService.countTotalCoin(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT);
        yesterdayStatistic.setTotalCoin(totalCoin.floatValue());

        //登录人数
        Integer loginNumCount = chatPetLoginLogService.getYesterdayLoginNumCount();
        yesterdayStatistic.setLoginNum(loginNumCount);

        //完成小游戏人数
        Integer yesterdayPlayGamePeopleAmount = chatPetMissionPoolService.getYesterdayPlayGamePeopleAmount();
        yesterdayStatistic.setPlayGameNum(yesterdayPlayGamePeopleAmount);

        //完成小游戏总次数
        Integer yesterPlayGameTotalAmount = chatPetMissionPoolService.getYesterPlayGameTotalAmount();
        yesterdayStatistic.setPlayGameTotalCount(yesterPlayGameTotalAmount);

        //昨日领取宠物人数
        Integer yesterdayAddNum = chatPetService.getYesterdayAddNum();
        yesterdayStatistic.setAddChatPetNum(yesterdayAddNum);

        //昨日领取宠物人数中领取过奖励人数
        Integer rewardNumFromYesterdayRegister = chatPetService.getRewardNumFromYesterdayRegister();
        yesterdayStatistic.setReceiveCoinNum(rewardNumFromYesterdayRegister);

        return yesterdayStatistic;
    }

}
