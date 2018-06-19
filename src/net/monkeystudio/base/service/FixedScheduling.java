package net.monkeystudio.base.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.Log;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by bint on 2018/6/8.
 */
@Service
public class FixedScheduling {


    /**
     * 创建一个定时任务
     * @param date 执行时间
     * @param runnable 运行内容
     */
    public Thread createFixedScheduling(Date date , Runnable runnable){
        Long time = DateUtils.getMillisecondTimestamp(date);

        Long currentTime = System.currentTimeMillis();

        if(currentTime > time){
            Log.e("FixedScheduling : scheduling  is outdated");
            return null;
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

                ScheduledFuture scheduledFuture = service.schedule(runnable,time - currentTime, TimeUnit.MILLISECONDS);

                service.shutdown();
            }
        });

        thread.setDaemon(true);
        thread.start();

        return thread;
    }

}
