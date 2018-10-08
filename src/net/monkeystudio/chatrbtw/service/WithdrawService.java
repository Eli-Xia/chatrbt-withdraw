package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.TBizException;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.StringUtil;
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
import org.springframework.transaction.annotation.Propagation;
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
        //企业付款失败
        private final static Integer TRANSFER_RESULT_FAIL = 0;
        //企业付款成功
        private final static Integer TRANSFER_RESULT_SUCCESS = 1;
        //企业付款重试
        private final static Integer TRANSFER_RESULT_RETRY = 2;
        //企业付款成功结果码
        private final static String SUCCESS_RESULT_CODE = "SUCCESS";
        //企业付款失败结果码
        private final static String FAIL_RESULT_CODE = "FAIL";
        //企业付款系统异常错误码
        private final static String DEFAULT_ERROR_CODE = "SYSTEMERROR";
        //付款重试最大次数
        private final static Integer RETRY_TIME = 3;
        //提现记录状态:正在进行中
        private final static Integer WITHDRAW_STATE_ON_GONG = 1;
        //提现记录状态:结束
        private final static Integer WITHDRAW_STATE_FINISH = 2;
        //系统商户订单号分隔符
        private final static String MCH_TRADE_NO_DELIMITER = "X";
    }

    /**
     * 企业付款结果类型  成功 失败 重试
     *
     * @param resultCode
     * @param errorCode
     * @return
     */
    private Integer getTransferResultType(String resultCode, String errorCode) {
        if (Const.SUCCESS_RESULT_CODE.equals(resultCode)) {
            return Const.TRANSFER_RESULT_SUCCESS;
        }
        if (Const.FAIL_RESULT_CODE.equals(resultCode) && Const.DEFAULT_ERROR_CODE.equals(errorCode)) {
            return Const.TRANSFER_RESULT_RETRY;

        } else return Const.TRANSFER_RESULT_FAIL;
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
     * 提现
     * <p>
     * <p>
     * 是否已经存在一个提现的流程
     * 1,提现金额最小金额100
     * 2,提现金额上限
     * 3,单个用户单日限额多少?500
     * 4,单个用户每天最多可以付款的次数? (微信可以设置)3
     * 5,付款时间间隔不得低于15S 15
     * 6,提现金额需要大于100?
     * 7,系统账户余额 > 提现金额 >= 用户账户余额 >= 100 ?
     * 8,当系统金额小于设定的阈值后是否需要发送邮件提醒?? no
     *
     * @param amount:提现金额
     */
    public void withdraw(BigDecimal amount) throws Exception {
        Integer fanId = UserContext.getFanId();

        //LOCK
        Integer lockId = userIdempotentService.add(fanId);

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

        //生成提现记录
        Withdraw record = new Withdraw();
        record.setCreateTime(new Date());
        record.setAmount(amount);
        record.setWxFanId(fanId);

        String mchTradeNo = this.createMchTradeNo(account.getId());
        record.setMchTradeNo(mchTradeNo);

        Integer withdrawId = this.save(record);

        //开启事务
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //用户账户扣款
                accountService.decrease(account.getId(), amount);

                //生成用户扣款流水
                accountFlowService.withdrawFlow(account.getId(), amount);

                //公司账户扣款
                bizAccountService.decrease(amount);

                //生成公司扣款流水
                bizAccountFlowService.transferFlow(BizAccountService.BIZ_ACCOUNT_ID, amount);

                WxFan wxFan = wxFanService.getById(fanId);
                String openId = wxFan.getWxFanOpenId();


                //调用微信企业付款,若返回"系统繁忙"需要使用相同的订单号重复调用,限制3次
                String resultCode = Const.FAIL_RESULT_CODE;//返回码
                String errorCode = Const.DEFAULT_ERROR_CODE;//错误码
                TransfersResult result = null;//结果
                int count = 0;//重复次数

                //重试,最多3次
                while (count < Const.RETRY_TIME) {
                    if (!Const.TRANSFER_RESULT_RETRY.equals(getTransferResultType(resultCode, errorCode))) {
                        break;
                    }

                    //企业付款
                    try{
                        result = wxTransferKitService.transfer(mchTradeNo, amount.intValue() * 100, openId);
                    }catch (Exception e){
                        throw new TBizException(e.getMessage());
                    }

                    resultCode = result.getResultCode();

                    errorCode = result.getErrCode();

                    count++;
                }

                //付款失败
                //FIXME 如果未认证必然被锁
                if (Const.TRANSFER_RESULT_FAIL.equals(getTransferResultType(resultCode, errorCode))) {

                    //另起一个事务不会被回滚
                    updateErrorCode(withdrawId, errorCode);

                    throw new TBizException("付款失败,错误代码:{?}" + errorCode);//抛出异常让事务回滚
                }


                //付款成功
                if (Const.TRANSFER_RESULT_SUCCESS.equals(getTransferResultType(resultCode, errorCode))) {

                    if (mchTradeNo.equals(result.getPartnerTradeNo())) {

                        Withdraw updateWd = getById(withdrawId);

                        updateWd.setWxPaymentNo(result.getPaymentNo());

                        updateWd.setPaymentTime(CommonUtils.string2Date(result.getPaymentTime(), "yyyy-MM-dd HH:mm:ss"));

                        update(updateWd);
                    }
                }
            }
        });

        //UNLOCK
        userIdempotentService.unlock(lockId);

    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void noteTransferRestul() {
        userIdempotentService.add(50);
    }

    public void testTxWithRet() {
        //事务返回的是企业付款的结果...
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                userIdempotentService.add(1000);
                noteTransferRestul();
            }
        });
    }

    /**
     * 记录企业付款结果
     * 将当前事务挂起,不会被回滚
     *
     * @param withdrawId
     * @param errorCode
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateErrorCode(Integer withdrawId, String errorCode) {
        Withdraw wd = this.getById(withdrawId);
        wd.setErrorCode(errorCode);
        this.update(wd);
    }


    /**
     * 生成系统订单号
     * 纳秒 + X(分割) + 账户id
     *
     * @param accountId:账户id
     * @return
     */
    private String createMchTradeNo(Integer accountId) {
        Long nano = System.nanoTime();
        return nano + Const.MCH_TRADE_NO_DELIMITER + accountId;
    }

    /**
     * 修正数据
     *
     * @param mchTradeNo
     * @param amount
     */
    @Transactional
    public void revise(String mchTradeNo, BigDecimal amount) {
        Integer accountId = Integer.parseInt(StringUtil.delimite(mchTradeNo,Const.MCH_TRADE_NO_DELIMITER)[1]);

        //用户账户扣款
        accountService.decrease(accountId, amount);

        //生成用户扣款流水
        accountFlowService.withdrawFlow(accountId, amount);

        //公司账户扣款
        bizAccountService.decrease(amount);

        //生成公司扣款流水
        bizAccountFlowService.transferFlow(BizAccountService.BIZ_ACCOUNT_ID, amount);

        //修改幂等表锁状态
        Account account = accountService.getById(accountId);
        Integer wxFanId = account.getWxFanId();

        //提现记录
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
