package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.exception.TBizException;
import net.monkeystudio.chatrbtw.entity.UserIdempotent;
import net.monkeystudio.chatrbtw.mapper.UserIdempotentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by bint on 2018/8/13.
 */
@Service
public class UserIdempotentService {
    @Autowired
    private TransactionTemplate txTemplate;
    @Autowired
    private UserIdempotentMapper userIdempotentMapper;

    public Integer add(Integer fanId) throws TBizException {
        UserIdempotent userIdempotent = new UserIdempotent();
        userIdempotent.setWxFanId(fanId);

        userIdempotent.setState(Contanst.LOCK_STATE);

        Integer result = userIdempotentMapper.insert(userIdempotent);

        if(result.intValue() == 0){
            throw new TBizException("请勿重复提交");
        }

        return userIdempotent.getId();
    }

    public void unlock(Integer id){
        this.updateState(id, Contanst.UNLOCK_STATE);
    }

    private Integer updateState(Integer id , Integer state){
        return userIdempotentMapper.updateState(id, state);
    }


    private static class Contanst{
        private final static Integer LOCK_STATE = 1;
        private final static Integer UNLOCK_STATE = 2;
    }


    @Transactional
    public void test() throws Exception{
        try{
            this.add(1);
        }catch (TBizException e){
            System.err.println(111);
        }
        System.err.println(222);
        Thread.sleep(15000);
        System.err.println(333);
    }

    public void test3(){
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                add(2);
                add(3);
                int i = 1 / 0;
            }
        });
    }



    @Transactional
    public void test2() throws Exception{
        Integer id = 1;
        UserIdempotent userIdempotent = userIdempotentMapper.selectByPrimaryKey(id);
        System.err.println("select result = " + userIdempotent == null ? "obj is null" : userIdempotent.getId());
        Integer count = userIdempotentMapper.updateByPrimaryKey(1);
        System.err.println(" update result count = " + count);
        System.out.println(1);

    }

}
