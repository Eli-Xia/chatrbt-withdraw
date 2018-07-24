package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.chatrbtw.entity.ChatPetExpFlow;
import net.monkeystudio.chatrbtw.entity.ChatPetExpFlow;
import net.monkeystudio.chatrbtw.mapper.ChatPetExpFlowMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetflow.ChatPetExpFlowResp;
import net.monkeystudio.chatrbtw.service.bean.chatpetflow.ChatPetExpFlowResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 宠物经验值流水
 * @author xiaxin
 */
@Service
public class ChatPetExpFlowService {
    @Autowired
    private ChatPetExpFlowMapper chatPetExpFlowMapper;

    /**
     * 生成流水基础方法
     * @param chatPetId     宠物id
     * @param actionType    产生流水的动作类型
     * @param note           流水信息
     */
    private void createBaseFlow(Integer chatPetId,Integer actionType,String note){
        ChatPetExpFlow chatPetExpFlow = new ChatPetExpFlow();
        chatPetExpFlow.setNote(note);
        chatPetExpFlow.setChatPetId(chatPetId);
        chatPetExpFlow.setExpActionType(actionType);
        chatPetExpFlow.setCreateTime(new Date());
        chatPetExpFlowMapper.insert(chatPetExpFlow);
    }

    /**
     * 体验游戏流水,经验值+XX
     */
    public void playGameFlow(Integer chaPetId,Float experience){
        String note = "体验游戏,经验值+" + ArithmeticUtils.keep2DecimalPlace(experience);
        this.createBaseFlow(chaPetId,FlowActionTypeService.ExpConsts.PLAY_GAME,note);
    }

    /**
     * 公众号打招呼流水,经验值+XX
     */
    public void wxPubSayHiFlow(Integer chaPetId,Float experience){
        String note = "完成公众号打招呼,经验值+" + ArithmeticUtils.keep2DecimalPlace(experience);
        this.createBaseFlow(chaPetId,FlowActionTypeService.ExpConsts.WX_PUB_SAY_HI,note);
    }

    /**
     * 赠送猫六六流水,经验值+XX
     */
    public void presentCatFlow(Integer chaPetId,Float experience){
        String note = "赠送一只猫六六,经验值+" + ArithmeticUtils.keep2DecimalPlace(experience);
        this.createBaseFlow(chaPetId,FlowActionTypeService.ExpConsts.PRESENT_A_LUCKY_CAT,note);
    }

    /**
     * 每日登录流水,经验值+XX
     */
    public void dailyLoginFlow(Integer chaPetId,Float experience){
        String note = "每日登录,经验值+" + ArithmeticUtils.keep2DecimalPlace(experience);
        this.createBaseFlow(chaPetId,FlowActionTypeService.ExpConsts.DAILY_LOGIN,note);
    }

    public List<ChatPetExpFlowResp> getChatPetExpFlowList(Integer chatPetId){
        List<ChatPetExpFlow> list = this.chatPetExpFlowMapper.selectExpFlow(chatPetId, 0, 100);
        List<ChatPetExpFlowResp> resps = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            ChatPetExpFlowResp resp = new ChatPetExpFlowResp();
            resp.setCreateTime(list.get(i).getCreateTime());
            resp.setNote(list.get(i).getNote());
            resp.setId(i + 1);
            resps.add(resp);
        }
        return resps;
    }

}
