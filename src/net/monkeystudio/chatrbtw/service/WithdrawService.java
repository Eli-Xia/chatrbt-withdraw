package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.exception.TBizException;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.UserContext;
import net.monkeystudio.chatrbtw.entity.Account;
import net.monkeystudio.chatrbtw.entity.BizAccount;
import net.monkeystudio.chatrbtw.entity.Withdraw;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.WithdrawMapper;
import net.monkeystudio.wx.service.WxTransferKitService;
import net.monkeystudio.wx.vo.transfers.TransfersResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;

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

    @Autowired
    private UserIdempotentService userIdempotentService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private TransactionTemplate txTemplate;

    private static class Const {
        //当天提现最大限额500
        private final static BigDecimal MAX_TOTAL_AMOUNT = new BigDecimal(500);
        //当天提现最大次数3
        private final static Integer MAX_COUNT = 3;
        //提现最小时间间隔15S
        private final static Long MIN_INTERVAL = 15000L;
    }


    public Integer save(Withdraw withdraw) {
        withdrawMapper.insert(withdraw);
        return withdraw.getId();
    }

    public void update(Withdraw withdraw) {
        withdrawMapper.update(withdraw);
    }

    public Withdraw getById(Integer id) {
        return withdrawMapper.selectByPrimaryKey(id);
    }


    /**
     * 提现操作方法
     *
     * @param amount:提现金额
     */
    public void operate(BigDecimal amount) throws TBizException {
        Integer fanId = UserContext.getFanId();

        Account account = accountService.getByPessimisticLock(fanId);

        BizAccount bizAccount = bizAccountService.getByPessimisticLock();

        if (amount.compareTo(new BigDecimal(100)) < 0) {
            throw new TBizException("提现金额应大于100");
        }

        if (amount.compareTo(account.getAmount()) > 0) {
            throw new TBizException("余额不足");
        }

        if (amount.compareTo(bizAccount.getAmount()) > 0) {
            throw new TBizException("系统繁忙,请稍后再试");
        }

        if (!this.isValidInterval(fanId)) {
            throw new TBizException("提现过于频繁,请稍后再试");
        }

        if (isReachMaxAmount(fanId, amount)) {
            throw new TBizException("超出当日提现最大额度");
        }

        if (isReachMaxCount(fanId)) {
            throw new TBizException("当日提现次数已满");
        }

        //提现相关操作
        Integer lockId = userIdempotentService.add(fanId);

        //生成提现记录
        Withdraw record = new Withdraw();
        record.setCreateTime(new Date());
        record.setAmount(amount);
        record.setWxFanId(fanId);

        String mchTradeNo = this.createMchTradeNo(account.getId());
        record.setMchTradeNo(mchTradeNo);

        Integer withdrawId = this.save(record);

        //在一个事务当中进行
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //用户账户扣款
                account.setAmount(account.getAmount().subtract(amount));
                accountService.update(account);

                //生成用户扣款流水
                accountFlowService.withdrawFlow(account.getId(),amount);

                //公司账户扣款
                bizAccount.setAmount(bizAccount.getAmount().subtract(amount));
                bizAccountService.update(bizAccount);

                //生成公司扣款流水
                bizAccountFlowService.transferFlow(BizAccountService.BIZ_ACCOUNT_ID,amount);

                //调用企业付款api进行付款
                WxFan wxFan = wxFanService.getById(fanId);
                String openId = wxFan.getWxFanOpenId();
                TransfersResult transfersResult = wxTransferKitService.transfer(mchTradeNo, amount.intValue() * 100, openId);
                //根据返回值是否正常来判断
            }
        });


        


        /*
            是否已经存在一个提现的流程
            1,提现金额最小金额100
            2,提现金额上限
            3,单个用户单日限额多少?500
            4,单个用户每天最多可以付款的次数? (微信可以设置)3
            5,付款时间间隔不得低于15S 15
            6,提现金额需要大于100?
            7,系统账户余额 > 提现金额 >= 用户账户余额 >= 100 ?
            8,当系统金额小于设定的阈值后是否需要发送邮件提醒?? no*/
    }


    /**
     * 生成系统订单号
     * @param accountId:账户id
     * @return
     */
    private String createMchTradeNo(Integer accountId){
        String noPrefix = CommonUtils.dateFormat(new Date(), "yyyyMMddHHmmss");
        return noPrefix + accountId;
    }




    /**
     * 当日提现限额500
     *
     * @param wxFanId
     * @return
     */
    private boolean isReachMaxAmount(Integer wxFanId, BigDecimal withdrawAmount) {
        Date now = new Date();

        Date beginTime = CommonUtils.dateStartTime(now);

        BigDecimal amount = withdrawMapper.sumByWxFanIdAndDate(wxFanId, beginTime);

        return amount.add(withdrawAmount).compareTo(Const.MAX_TOTAL_AMOUNT) > 0;
    }

    /**
     * 当日提现次数是否已满
     *
     * @param wxFanId
     * @return
     */
    private boolean isReachMaxCount(Integer wxFanId) {
        Date now = new Date();

        Date beginTime = CommonUtils.dateStartTime(now);

        Integer count = withdrawMapper.countByWxFanIdAndDate(wxFanId, beginTime);

        return count.equals(Const.MAX_COUNT);
    }


    /**
     * 提现时间间隔是否正确
     *
     * @param wxFanId
     * @return
     */
    private boolean isValidInterval(Integer wxFanId) {
        //查询上一次提现成功的时间. 然后跟现在的时间进行比对.
        Date now = new Date();

        Date beginTime = CommonUtils.dateStartTime(now);

        Date lastPayTime = withdrawMapper.selectLastPayTime(wxFanId, beginTime);

        Long interval = DateUtils.getBetweenTwoDate(now, lastPayTime);

        return interval.compareTo(Const.MIN_INTERVAL) >= 0;
    }


}
