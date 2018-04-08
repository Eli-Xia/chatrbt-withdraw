package net.monkeystudio.portal.controller.req.chatrotot;

import java.util.List;

/**
 * Created by bint on 03/01/2018.
 */
public class AddChatRobot {

    private String nickname;
    private Integer gender;
    private List<Integer> characterList;

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
