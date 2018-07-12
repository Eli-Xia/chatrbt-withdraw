package net.monkeystudio.chatrbtw.service.bean.gamecenter;

/**
 * 猫市摊位信息
 * @author xiaxin
 */
public class ChatPetCenterStallResp {
    private Integer state;// 0:未完成 1:完成 null:不需要状态
    private Integer orderNum;//序号

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
