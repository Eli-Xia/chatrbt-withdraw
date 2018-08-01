package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.MsgTemplateForm;
import net.monkeystudio.chatrbtw.mapper.MsgTemplateFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by bint on 2018/7/31.
 */
@Service
public class MsgTemplateFormService {

    @Autowired
    private MsgTemplateFormMapper msgTemplateFormMapper;

    /**
     * 获取可用的消息表单Id
     * @param wxFanId
     * @return
     */
    public MsgTemplateForm getEnable(Integer wxFanId){

        Date currentTime = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);

        //有效期只有七天
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        //开始时间
        Date startTime = calendar.getTime();

        MsgTemplateForm msgTemplateForm = this.getByWxFanId(wxFanId , startTime, Constant.UNUSED_STATE);

        return msgTemplateForm;
    }

    private void save(MsgTemplateForm msgTemplateForm){
        msgTemplateFormMapper.insert(msgTemplateForm);
    }

    private MsgTemplateForm getByWxFanId(Integer wxFanId ,Date createTime ,Integer state){
        return msgTemplateFormMapper.selectByWxFanIdAndCreateDate(wxFanId, createTime, state);
    }

    /**
     * 修改消息模版的状态
     * @param msgTemplateFormId
     */
    public void updateState(Integer msgTemplateFormId){
        Date date = new Date();
        msgTemplateFormMapper.updateState(msgTemplateFormId , date , Constant.HAVE_USED_STATE);
    }

    /**
     * 保存表单
     * @param formId
     * @param wxFanId
     */
    public void save(String formId , Integer wxFanId){
        MsgTemplateForm msgTemplateForm = new MsgTemplateForm();

        msgTemplateForm.setFormId(formId);
        msgTemplateForm.setWxFanId(wxFanId);

        msgTemplateForm.setCreateTime(new Date());
        msgTemplateForm.setState(Constant.UNUSED_STATE);

        this.save(msgTemplateForm);
    }

    static class Constant{
        public final static Integer UNUSED_STATE = 0;
        public final static Integer HAVE_USED_STATE = 1;
        public final static Integer EXPIRED_STATE = 2;

    }
}
