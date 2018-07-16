package net.monkeystudio.chatrbtw.service;

import org.springframework.stereotype.Service;

/**
 * 流水变化类型常量
 * @author xiaxin
 */
@Service
public class FlowActionTypeService {
    //猫饼流水
    static class CoinConsts{
        public final static Integer JOIN_AUCTION = 1;//参与竞拍
        public final static Integer FAIL_AUCTION = 2;//竞拍未中标
        public final static Integer DAILY_REWARD= 3;//等级奖励日常领取
    }


    //经验值流水
    static class ExpConsts{
        public final static Integer PLAY_GAME = 1;//体验游戏
        public final static Integer WX_PUB_SAY_HI = 2;//完成公众号打招呼
        public final static Integer PRESENT_A_LUCKY_CAT = 3;//赠送猫六六
        public final static Integer DAILY_LOGIN = 4;//每日登录
    }


}
