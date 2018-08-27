package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.mapper.BizAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BizAccountService {
    @Autowired
    private BizAccountMapper bizAccountMapper;
}
