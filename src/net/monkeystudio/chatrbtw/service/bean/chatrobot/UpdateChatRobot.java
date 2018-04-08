package net.monkeystudio.chatrbtw.service.bean.chatrobot;

import java.util.List;

/**
 * Created by bint on 05/01/2018.
 */
public class UpdateChatRobot {

    private Integer id;
    private String nickname;
    private Integer gender;
    private List<Integer> characterList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public List<Integer> getCharacterList() {
        return characterList;
    }

    public void setCharacterList(List<Integer> characterList) {
        this.characterList = characterList;
    }

}
