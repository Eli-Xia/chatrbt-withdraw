package net.monkeystudio.chatrbtw.entity;

/**
 * Created by bint on 03/01/2018.
 */
public class RRobotCharacter {
    private Integer id;
    private Integer chatRobotId;
    private Integer chatRobotCharacterId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatRobotId() {
        return chatRobotId;
    }

    public void setChatRobotId(Integer chatRobotId) {
        this.chatRobotId = chatRobotId;
    }

    public Integer getChatRobotCharacterId() {
        return chatRobotCharacterId;
    }

    public void setChatRobotCharacterId(Integer chatRobotCharacterId) {
        this.chatRobotCharacterId = chatRobotCharacterId;
    }
}
