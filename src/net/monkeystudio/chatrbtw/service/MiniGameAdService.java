package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.chatrbtw.entity.MiniGameAd;
import net.monkeystudio.chatrbtw.mapper.MiniGameAdMapper;
import net.monkeystudio.chatrbtw.service.bean.minigamead.AddMiniGameAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MiniGameAdService {
    @Autowired
    private MiniGameAdMapper miniGameAdMapper;

    @Autowired
    private UploadService uploadService;

    private final static Integer BEFORE_AUDIT_STATE = 1;//微信审核前
    private final static Integer AFTER_AUDIT_STATE = 2;//微信审核后
    private final static String DIRCTORY_NAME = "/chat_pet/mini_game_ad";//图片存放路径


    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    public String uploadPic(MultipartFile multipartFile ){
        return uploadService.uploadPic(multipartFile, DIRCTORY_NAME ,String.valueOf(System.currentTimeMillis()));
    }

    public MiniGameAd getById(Integer id){
        return miniGameAdMapper.selectById(id);
    }

    public Integer add(AddMiniGameAd addMiniGameAd){
        MiniGameAd miniGameAd = BeanUtils.copyBean(addMiniGameAd, MiniGameAd.class);

        miniGameAd.setState(BEFORE_AUDIT_STATE);

        return this.save(miniGameAd);
    }

    /**
     * 修改
     * @param miniGameAd
     * @return
     */
    public Integer update(MiniGameAd miniGameAd){
        return miniGameAdMapper.update(miniGameAd);
    }


    public void changeState(Integer id){
        MiniGameAd miniGameAd = this.getById(id);
        
    }

    public List<MiniGameAd> getMiniGameList(){
        return miniGameAdMapper.selectAll();
    }

    private Integer save(MiniGameAd miniGameAd){
        return miniGameAdMapper.insert(miniGameAd);
    }

}
