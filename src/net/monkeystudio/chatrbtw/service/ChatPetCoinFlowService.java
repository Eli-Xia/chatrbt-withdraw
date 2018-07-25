package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.chatrbtw.entity.ChatPetCoinFlow;
import net.monkeystudio.chatrbtw.mapper.ChatPetCoinFlowMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetflow.ChatPetCoinFlowResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 猫饼流水
 * @author xiaxin
 */
@Service
public class ChatPetCoinFlowService {
    @Autowired
    private ChatPetCoinFlowMapper chatPetCoinFlowMapper;

    /**
     * 生成流水基础方法
     * @param chatPetId     宠物id
     * @param actionType    产生流水的动作类型
     * @param note           流水信息
     * @param amount         变化的数额
     */
    private void createBaseFlow(Integer chatPetId,Integer actionType,String note,Float amount){
        ChatPetCoinFlow chatPetCoinFlow = new ChatPetCoinFlow();
        chatPetCoinFlow.setNote(note);
        chatPetCoinFlow.setChatPetId(chatPetId);
        chatPetCoinFlow.setCoinActionType(actionType);
        chatPetCoinFlow.setCreateTime(new Date());
        chatPetCoinFlow.setAmount(amount);
        chatPetCoinFlowMapper.insert(chatPetCoinFlow);
    }

    /**
     * 参与竞拍流水,猫饼-XX
     */
    public void auctionFlow(Integer chaPetId,Float coin){
        String note = "参与竞拍,猫饼-" + ArithmeticUtils.keep2DecimalPlace(coin);
        this.createBaseFlow(chaPetId,FlowActionTypeService.CoinConsts.JOIN_AUCTION,note,-coin);
    }

    /**
     * 竞拍未中标流水,退还猫饼+XX
     */
    public void auctionFailFlow(Integer chaPetId,Float coin){
        //String note = "竞拍未中标,退还猫饼+" + coin;
        String note = "竞拍未中标";
        this.createBaseFlow(chaPetId,FlowActionTypeService.CoinConsts.FAIL_AUCTION,note,null);
    }

    /**
     * 日常领取流水,猫饼+XX
     */
    public void dailyRewardFlow(Integer chaPetId,Float coin){
        String note = "日常领取,猫饼+" + ArithmeticUtils.keep2DecimalPlace(coin);
        this.createBaseFlow(chaPetId,FlowActionTypeService.CoinConsts.DAILY_REWARD,note,coin);
    }

    /**
     * 注册奖励
     * @param chatPetId
     */
    public void registerRewardFlow(Integer chatPetId){
        String note = "日常领取,猫饼+" + 0.01;
        this.createBaseFlow(chatPetId,FlowActionTypeService.CoinConsts.REGISTER_REWARD,note,0.01F);
    }

    public List<ChatPetCoinFlowResp> getChatPetCoinFlowList(Integer chatPetId){
        List<ChatPetCoinFlow> list = this.chatPetCoinFlowMapper.selectCoinFlow(chatPetId, 0, 100);
        List<ChatPetCoinFlowResp> resps = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            ChatPetCoinFlowResp resp = new ChatPetCoinFlowResp();
            resp.setCreateTime(list.get(i).getCreateTime());
            resp.setNote(list.get(i).getNote());
            resp.setId(i + 1);
            resps.add(resp);
        }
        return resps;
    }
}
