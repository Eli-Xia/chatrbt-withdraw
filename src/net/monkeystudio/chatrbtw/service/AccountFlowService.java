package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.AccountFlow;
import net.monkeystudio.chatrbtw.mapper.AccountFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountFlowService {
    @Autowired
    private AccountFlowMapper accountFlowMapper;

    private static class Constant {
        private final static Integer DIVIDEND = 0;//账户资金变化原因 分红
        private final static Integer WITHDRAW = 1;//账户资金变化原因 提现
    }

    /**
     * 生成一条分红流水
     *
     * @param accountId:账户id
     * @param amount:流水数额
     */
    public void dividendFlow(Integer accountId, BigDecimal amount) {
        AccountFlow accountFlow = new AccountFlow();
        accountFlow.setAccountId(accountId);
        accountFlow.setAmount(amount);
        accountFlow.setCreateTime(new Date());
        accountFlow.setActionType(Constant.DIVIDEND);
        accountFlowMapper.insert(accountFlow);
    }

    /**
     * 生成一条提现流水
     *
     * @param accountId:账户id
     * @param amount:流水数额
     */
    public void withdrawFlow(Integer accountId, BigDecimal amount) {
        AccountFlow accountFlow = new AccountFlow();
        accountFlow.setAmount(amount);
        accountFlow.setAccountId(accountId);
        accountFlow.setCreateTime(new Date());
        accountFlow.setActionType(Constant.WITHDRAW);
        accountFlowMapper.insert(accountFlow);
    }
}
