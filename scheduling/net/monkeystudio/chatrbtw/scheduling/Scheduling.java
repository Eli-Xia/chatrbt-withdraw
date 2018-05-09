package net.monkeystudio.chatrbtw.scheduling;

import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.mapper.GlobalConfigMapper;
import net.monkeystudio.chatrbtw.service.ChatRobotService;
import net.monkeystudio.chatrbtw.service.EthnicGroupsService;
import net.monkeystudio.chatrbtw.service.OpLogService;
import net.monkeystudio.chatrbtw.service.PushMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by bint on 09/01/2018.
 */
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

    /**
     * 删除无效的机器人
     */
    @Scheduled(cron="0 0 2 * * ?") //当日2点
    public void deleteRobotInfo(){
    	opLogService.systemOper(AppConstants.OP_LOG_TAG_S_DELETE_INVALID_ROBOT, "删除无效机器人任务启动。");
        chatRobotService.deleteInvalidRobot();
    }


    /**
     * 删除无效的机器人
     */
    @Scheduled(cron="0 0 0 * * ?") //当日晚上12点重置限制
    public void resetDailyRestrictions(){
        opLogService.systemOper(AppConstants.OP_LOG_TAG_S_RESET_ETHNIC_GROUPS_DAILY_RESTRICTIONSv, "重置当日族群接入个数限制");
        ethnicGroupsService.resetDailyRestrictions();
    }

    
    /*@Scheduled(cron="* 0/30 * * * ? ") //间隔30分钟执行
    public void checkNeedToSendMessageHandle(){
        pushMessage.checkNeedToSendMessageHandle();

    }*/
    
    /**
     * sql心跳
     */
    @Scheduled(cron="0/50 * * * * ? ") //间隔50秒执行  
    public void sqlHeartBeat(){
        Log.d("Sql heartbeat, period 50 seconds.");
        globalConfigMapper.selectTest();
        
    }

}
