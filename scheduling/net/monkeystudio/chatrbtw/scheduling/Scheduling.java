/*
package net.monkeystudio.chatrbtw.scheduling;

import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.mapper.GlobalConfigMapper;
import net.monkeystudio.chatrbtw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

*/
/**
 * Created by bint on 09/01/2018.
 *//*

@Component
public class Scheduling {


    @Autowired
    private ChatRobotService chatRobotService;
   
    @Autowired
    private OpLogService opLogService;
    
    @Autowired
    private PushMessageService pushMessage;

    @Autowired
	private GlobalConfigMapper globalConfigMapper;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    */
/**
     * 删除无效的机器人
     *//*

    @Scheduled(cron="0 0 2 * * ?") //当日2点
    public void deleteRobotInfo(){
    	opLogService.systemOper(AppConstants.OP_LOG_TAG_S_DELETE_INVALID_ROBOT, "删除无效机器人任务启动。");
        chatRobotService.deleteInvalidRobot();
    }


    */
/**
     * 重置当日族群接入个数
     *//*

    @Scheduled(cron="0 0 0 * * ?") //当日晚上12点重置族群接入个数
    public void resetDailyRestrictions(){
        opLogService.systemOper(AppConstants.OP_LOG_TAG_S_RESET_ETHNIC_GROUPS_DAILY_RESTRICTIONS, "重置当日族群接入个数限制");
        ethnicGroupsService.resetDailyRestrictions();
    }


    */
/*@Scheduled(cron="* 0/30 * * * ? ") //间隔30分钟执行
    public void checkNeedToSendMessageHandle(){
        pushMessage.checkNeedToSendMessageHandle();

    }*//*


    */
/**
     * sql心跳
     *//*

    @Scheduled(cron="0/50 * * * * ? ") //间隔50秒执行  
    public void sqlHeartBeat(){
        Log.d("Sql heartbeat, period 50 seconds.");
        globalConfigMapper.selectTest();
        
    }


    @Scheduled(cron="0 0 0,2,4,6,8,10,12,14,16,18,20,22 * * ? ")//每天两个小时分发一次等级奖励
    public void generateLevelReward(){
        opLogService.systemOper(AppConstants.OP_LOG_TAG_S_GENERATE_LEVEL_REWARD, "生成等级奖励");
        Log.i("generateLevelReward method run ! ");

        chatPetRewardService.generateLevelReward();
    }

    @Scheduled(cron="0 0 0 * * ?") //当日12点去掉过期的任务奖励
    public void deleteMissionReward(){
        chatPetRewardService.expireAward();
    }
}
*/
