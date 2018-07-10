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

        //监听咨询任务是否完成
        chatPetMissionPoolService.initSubscribe();

        //删除无用机器人
        chatRobotService.deleteRobotInfoTask();

        //重置族群接入个数
        ethnicGroupsService.resetDailyRestrictionsTask();

        //消费等级消费队列
        chatPetRewardService.comsumeLevelReward();

        //删除过时任务
        chatPetRewardService.deleteMissionRewardTask();

        //生成等级奖励
        chatPetRewardService.generateLevelRewardTask();

        //心跳
        sqlHeartBeatService.sqlHeartBeatTask();
        //}
    }
}
