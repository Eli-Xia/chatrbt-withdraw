package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.DividendMsg;
import net.monkeystudio.chatrbtw.mapper.DividendMsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DividendMsgService {
    @Autowired
    private DividendMsgMapper dividendMsgMapper;

    public List<DividendMsg> getMsgs() {
        return dividendMsgMapper.selectAll();
    }

    public void save(String content, String desc) {
        DividendMsg dividendMsg = new DividendMsg();
        dividendMsg.setContent(content);
        dividendMsg.setDescription(desc);
        dividendMsgMapper.insert(dividendMsg);
    }

    public void update(Integer id, String content, String desc) {
        DividendMsg dm = this.getById(id);
        dm.setDescription(desc);
        dm.setContent(content);
        dividendMsgMapper.insert(dm);
    }

    public DividendMsg getById(Integer id) {
        return dividendMsgMapper.selectByPrimaryKey(id);
    }

    public void delete(Integer id) {
        dividendMsgMapper.deleteByPrimaryKey(id);
    }
}
