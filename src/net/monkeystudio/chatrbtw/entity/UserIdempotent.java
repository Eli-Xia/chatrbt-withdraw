package net.monkeystudio.chatrbtw.entity;

//表名e_user_idempotent
public class UserIdempotent {
    private Integer id;
    private Integer wxFanId;
    private Integer state;

    public Integer getId() {
        return id;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

}