package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.BizAccountFlow;
import net.monkeystudio.chatrbtw.mapper.BizAccountFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class BizAccountFlowService {
    @Autowired
    private BizAccountFlowMapper bizAccountFlowMapper;

    private static class Constant {
        private final static Integer TRANSFER = 0;//账户资金变化原因 付款
        private final static Integer RECHARGE = 1;//账户资金变化原因 充值
    }

    /**
     * 生成一条企业付款流水
     *
     * @param bizAccountId:公司账户id
     * @param amount:流水数额
     */
    public void transferFlow(Integer bizAccountId, BigDecimal amount) {
        BizAccountFlow bizAccountFlow = new BizAccountFlow();
        bizAccountFlow.setBizAccountId(bizAccountId);
        bizAccountFlow.setAmount(amount);
        bizAccountFlow.setCreateTime(new Date());
        bizAccountFlow.setActionType(Constant.TRANSFER);
        bizAccountFlowMapper.insert(bizAccountFlow);
    }

    /**
     * 生成一条充值流水
     *
     * @param bizAccountId:公司账户id
     * @param amount:流水数额
     */
    public void rechargeFlow(Integer bizAccountId, BigDecimal amount) {
        BizAccountFlow bizAccountFlow = new BizAccountFlow();
        bizAccountFlow.setBizAccountId(bizAccountId);
        bizAccountFlow.setAmount(amount);
        bizAccountFlow.setCreateTime(new Date());
        bizAccountFlow.setActionType(Constant.RECHARGE);
        bizAccountFlowMapper.insert(bizAccountFlow);
    }
}
