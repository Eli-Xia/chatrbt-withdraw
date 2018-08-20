package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.chatrbtw.entity.UserIdempotent;
import net.monkeystudio.chatrbtw.mapper.UserIdempotentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/8/13.
 */
@Service
public class UserIdempotentService {

    @Autowired
    private UserIdempotentMapper userIdempotentMapper;

    public Integer add(Integer userId) throws BizException {
        UserIdempotent userIdempotent = new UserIdempotent();
        userIdempotent.setUserId(userId);

        userIdempotent.setState(Contanst.LOCK_STATE);

        Integer result = userIdempotentMapper.insert(userIdempotent);

        if(result.intValue() == 0){
            throw new BizException("请勿重复提交");
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

}
