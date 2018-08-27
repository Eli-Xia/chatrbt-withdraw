package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.chatrbtw.entity.AccountIdempotent;
import net.monkeystudio.chatrbtw.entity.UserIdempotent;
import net.monkeystudio.chatrbtw.mapper.AccountIdempotentMapper;
import net.monkeystudio.chatrbtw.mapper.UserIdempotentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/8/13.
 */
@Service
public class AccountIdempotentService {

    @Autowired
    private AccountIdempotentMapper accountIdempotentMapper;

    public Integer add(Integer accountId) throws BizException {
        AccountIdempotent accountIdempotent = new AccountIdempotent();
        accountIdempotent.setAccountId(accountId);

        accountIdempotent.setState(Contanst.LOCK_STATE);

        Integer result = accountIdempotentMapper.insert(accountIdempotent);

        if(result.intValue() == 0){
            throw new BizException("请勿重复提交");
        }

        return accountIdempotent.getId();
    }

    public void unlock(Integer id){
        this.updateState(id, Contanst.UNLOCK_STATE);
    }

    private Integer updateState(Integer id , Integer state){
        return accountIdempotentMapper.updateState(id, state);
    }


    private static class Contanst{
        private final static Integer LOCK_STATE = 1;
        private final static Integer UNLOCK_STATE = 2;
    }

}
