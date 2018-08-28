package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.BizAccount;
import net.monkeystudio.chatrbtw.mapper.BizAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BizAccountService {
    @Autowired
    private BizAccountMapper bizAccountMapper;

    /**
     * 悲观锁获取账户
     * @param id
     * @return
     */
    public BizAccount getByPessimisticLock(Integer id){
        return bizAccountMapper.selectForUpdate(id);
    }

    public BizAccount getById(Integer id){
        return bizAccountMapper.selectByPrimaryKey(id);
    }

    public Integer update(BizAccount bizAccount){
        return bizAccountMapper.updateByPrimaryKey(bizAccount);
    }
}
