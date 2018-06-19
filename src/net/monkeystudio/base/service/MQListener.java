package net.monkeystudio.base.service;

import net.monkeystudio.base.SpringContextService;
import net.monkeystudio.chatrbtw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/6/1.
 */
@Service
public class MQListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatRobotService chatRobotService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private SqlHeartBeatService sqlHeartBeatService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //if(event.getApplicationContext().getParent() == null){
        chatPetRewardService.comsumeLevelReward();
        chatPetMissionPoolService.initSubscribe();
        chatRobotService.deleteRobotInfoTask();
        ethnicGroupsService.resetDailyRestrictionsTask();
        chatPetRewardService.deleteMissionRewardTask();
        chatPetRewardService.generateLevelRewardTask();
        sqlHeartBeatService.sqlHeartBeatTask();
        //}
    }
}
