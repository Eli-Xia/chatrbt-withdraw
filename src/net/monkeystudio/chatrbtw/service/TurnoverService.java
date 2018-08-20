package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.Turnover;
import net.monkeystudio.chatrbtw.mapper.TurnoverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by bint on 2018/8/20.
 */
@Service
public class TurnoverService {

    @Autowired
    private TurnoverMapper turnoverMapper;

    /**
     * 公司充值
     * @param userId
     * @param amount
     * @param sourceId
     * @return
     */
    public Integer addRecharge(Integer userId ,Double amount ,Integer sourceId){

        Turnover turnover = new Turnover();

        turnover.setUserId(userId);
        turnover.setType(Constants.OUTSIDE_TO_COMPANY);
        turnover.setAccount(amount);
        turnover.setCreateTime(new Date());
        turnover.setSourceId(sourceId);
        turnover.setSourceType(Constants.RECHARGE);

        return this.save(turnover);
    }


    public Integer addWithdraw(Integer userId ,Double amount ,Integer sourceId){

        Turnover turnover = new Turnover();

        turnover.setUserId(userId);
        turnover.setType(Constants.COMPANY_TO_OUTSIDE);
        turnover.setAccount(amount);
        turnover.setCreateTime(new Date());
        turnover.setSourceId(sourceId);
        turnover.setSourceType(Constants.WITHDRAW);

        return this.save(turnover);
    }

    public Integer save(Turnover turnover){
        return turnoverMapper.insert(turnover);
    }

    private static class Constants{
        private final static Integer COMPANY_TO_OUTSIDE = 3;//公司到系统之外
        private final static Integer OUTSIDE_TO_COMPANY = 4;//系统之外转到公司


        private final static Integer WITHDRAW = 1;//提现
        private final static Integer RECHARGE = 2;//充值
    }
}
