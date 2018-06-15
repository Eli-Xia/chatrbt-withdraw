package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.SpringContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * @author xiaxin
 */
@Service
public class TaskTestService {
    @Autowired
    private SpringContextService springContextService;

    private String CLASS_NAME_PREFIX = "net.monkeystudio.chatrbtw.service.";

    public void test()throws Exception{
        //前缀加上类名
        String className = CLASS_NAME_PREFIX + "ChatPetRewardService";
        Class<?> aClass = Class.forName(className);
        Object bean = springContextService.getBean(aClass);
        Method method = bean.getClass().getDeclaredMethod("generateLevelReward");
        method.invoke(bean);
        System.out.println(1);
        //写一个ScheduledTaskService 定时任务的处理  在里面进行处理就ok了...

    }
}
