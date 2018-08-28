package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.Withdraw;
import net.monkeystudio.chatrbtw.mapper.WithdrawMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WithdrawService {
    @Autowired
    private WithdrawMapper withdrawMapper;

    public Integer save(Withdraw withdraw){
        withdrawMapper.insert(withdraw);
        return withdraw.getId();
    }

    public void update(Withdraw withdraw){
        withdrawMapper.update(withdraw);
    }

    public Withdraw getById(Integer id){
        return withdrawMapper.selectByPrimaryKey(id);
    }
}
