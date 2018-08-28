package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.Account;
import net.monkeystudio.chatrbtw.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;


    /**
     * 悲观锁查询账户
     * @param id
     * @return
     */
    public Account getByPessimisticLock(Integer id){
        return accountMapper.selectForUpdate(id);
    }

    public Account getById(Integer id){
        return accountMapper.selectByPrimaryKey(id);
    }

    public Integer update(Account account){
        return accountMapper.updateByPrimaryKey(account);
    }

}
