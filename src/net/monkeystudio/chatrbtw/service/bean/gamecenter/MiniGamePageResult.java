package net.monkeystudio.chatrbtw.service.bean.gamecenter;

import java.util.ArrayList;
import java.util.List;

public class MiniGamePageResult {
    private List<MiniGameVO> list = new ArrayList<>();
    private Integer total;

    public List<MiniGameVO> getList() {
        return list;
    }

    public void setList(List<MiniGameVO> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
