package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.TBizException;
import net.monkeystudio.chatrbtw.entity.BizAccount;
import net.monkeystudio.chatrbtw.mapper.BizAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BizAccountService {
    @Autowired
    private BizAccountMapper bizAccountMapper;

    //系统账户id为1
    public static final Integer BIZ_ACCOUNT_ID = 1;

    /**
     * 悲观锁获取账户
     *
     * @param
     * @return
     */
    public BizAccount getByPessimisticLock() {
        return bizAccountMapper.selectForUpdate(BIZ_ACCOUNT_ID);
    }

    public BizAccount getBizAccount() {
        return bizAccountMapper.selectByPrimaryKey(BIZ_ACCOUNT_ID);
    }

    public Integer update(BizAccount bizAccount) {
        return bizAccountMapper.updateByPrimaryKey(bizAccount);
    }

    /**
     * 扣款
     * @param amount:金额
     * @throws TBizException
     */
    public void decrease(BigDecimal amount) throws TBizException {
        Integer count = bizAccountMapper.decrease(BIZ_ACCOUNT_ID, amount);
        if(count <= 0){
            throw new TBizException("公司账户扣款失败");
        }
    }


}
