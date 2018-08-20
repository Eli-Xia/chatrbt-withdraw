package net.monkeystudio.chatrbtw.entity;

//表名e_user_idempotent
public class UserIdempotent {
    private Integer id;
    private Integer userId;
    private Integer state;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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