package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.chatrbtw.UserContext;
import net.monkeystudio.chatrbtw.entity.Account;
import net.monkeystudio.chatrbtw.entity.Withdraw;
import net.monkeystudio.chatrbtw.mapper.WithdrawMapper;
import net.monkeystudio.wx.service.WxTransferKitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WithdrawService {
    @Autowired
    private WithdrawMapper withdrawMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BizAccountService bizAccountService;

    @Autowired
    private AccountFlowService accountFlowService;

    @Autowired
    private BizAccountFlowService bizAccountFlowService;

    @Autowired
    private WxTransferKitService wxTransferKitService;


    public Integer save(Withdraw withdraw){
        withdrawMapper.insert(withdraw);
        return withdraw.getId();
    }

    public void update(Withdraw withdraw){
        withdrawMapper.update(withdraw);
    }

    public Withdraw getById(Integer id){
        return withdrawMapper.selectByPrimaryKey(id);
    }


    /**
     * 提现操作方法
     * @param amount:提现金额
     */
    public void operate(BigDecimal amount) throws BizException{
        Integer fanId = UserContext.getFanId();
        Account account = accountService.getByPessimisticLock(fanId);
        if(amount.compareTo(new BigDecimal(100)) < 0){
            throw new BizException("提现金额应大于100");
        }
        /*  1,提现金额最小金额100
            2,提现金额上限
            3,单个用户单日限额多少?500
            4,单个用户每天最多可以付款的次数? (微信可以设置)3
            5,付款时间间隔不得低于15S 15
            6,提现金额需要大于100?
            7,系统账户余额 > 提现金额 >= 用户账户余额 >= 100 ?
            8,当系统金额小于设定的阈值后是否需要发送邮件提醒?? no*/
    }






}
