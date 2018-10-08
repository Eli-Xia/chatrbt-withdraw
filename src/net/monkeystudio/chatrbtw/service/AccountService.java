package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.TBizException;
import net.monkeystudio.chatrbtw.entity.Account;
import net.monkeystudio.chatrbtw.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;


    /**
     * 悲观锁查询账户
     *
     * @param wxFanId:粉丝id
     * @return
     */
    public Account getByPessimisticLock(Integer wxFanId) {
        return accountMapper.selectForUpdate(wxFanId);
    }

    public Account getById(Integer id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    public Integer update(Account account) {
        return accountMapper.updateByPrimaryKey(account);
    }

    /**
     * 扣款
     * @param accountId:账户id
     * @param amount:金额
     * @throws TBizException
     */
    public void decrease(Integer accountId, BigDecimal amount) throws TBizException {
        Integer count = accountMapper.decrease(accountId, amount);
        if (count <= 0) {
            throw new TBizException("扣款失败,账户id=" + accountId);
        }
    }


}
