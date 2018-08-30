package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.BizAccount;
import net.monkeystudio.chatrbtw.mapper.BizAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
