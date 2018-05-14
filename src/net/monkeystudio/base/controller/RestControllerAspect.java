package net.monkeystudio.base.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * 该类实现所有controller中所有restApi的通用逻辑，例如异常处理
 * Created by liujinhua on 2016/9/5.
 */
public class RestControllerAspect {
	
    @Autowired
    RespHelper respHelper;
    
    public RespBase apiWrap(ProceedingJoinPoint pjp){
        RespBase resp = null;
        try {
            resp = (RespBase) pjp.proceed();
        } catch (BizException e){
            Log.e(e);
            resp = respHelper.failed(e.getBizExceptionMsg());
        }catch (Throwable t){
            Log.e(t);
            resp = respHelper.failed(Msg.text("common.sys.unknown.exception"));
        }
        return resp;
    }
}
