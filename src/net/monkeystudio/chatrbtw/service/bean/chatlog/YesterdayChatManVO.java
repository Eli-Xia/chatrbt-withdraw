package net.monkeystudio.chatrbtw.service.bean.chatlog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 * @date 2018/1/17 17:17
 * 昨日聊天人数页面数据
 */
public class YesterdayChatManVO {
    private Long chatManNum;//昨日聊天人数

    private Long boostManNum;//新增人数

    private List<ChatLogChartNode> nodes = new ArrayList<>();


    public Long getChatManNum() {
        return chatManNum;
    }

    public void setChatManNum(Long chatManNum) {
        this.chatManNum = chatManNum;
    }

    public Long getBoostManNum() {
        return boostManNum;
    }

    public void setBoostManNum(Long boostManNum) {
        this.boostManNum = boostManNum;
    }

    public List<ChatLogChartNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ChatLogChartNode> nodes) {
        this.nodes = nodes;
    }
}
