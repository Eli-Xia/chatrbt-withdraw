package net.monkeystudio.chatrbtw.service.bean.chatlog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 * @date 2018/1/17 17:17
 * 昨日聊天次数页面数据
 */
public class YesterdayChatNumVO {
    private Long chatNum;//昨日聊天次数

    private Long boostNum;//新增次数

    private List<ChatLogChartNode> nodes = new ArrayList<>();


    public Long getChatNum() {
        return chatNum;
    }

    public void setChatNum(Long chatNum) {
        this.chatNum = chatNum;
    }

    public Long getBoostNum() {
        return boostNum;
    }

    public void setBoostNum(Long boostNum) {
        this.boostNum = boostNum;
    }

    public List<ChatLogChartNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ChatLogChartNode> nodes) {
        this.nodes = nodes;
    }
}
