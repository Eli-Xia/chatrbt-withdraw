package net.monkeystudio.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2017/11/5.
 */
@Service
public class TaskExecutor {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    public void execute(Runnable runnable){
        threadPoolTaskExecutor.execute(runnable);
    }

}
