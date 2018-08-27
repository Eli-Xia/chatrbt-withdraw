package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;
}
