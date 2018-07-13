package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import net.monkeystudio.chatrbtw.mapper.WxMiniGameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaxin
 */
@Service
public class WxMiniGameService {
    @Autowired
    private WxMiniGameMapper wxMiniGameMapper;
    @Autowired
    private COSService cosService;
    @Autowired
    private UploadService uploadService;

    private final static String WX_MINI_GAME_HEAD_IMG_COS_PATH = "/lucky-cat/mini-game/head-img";
    private final static String WX_MINI_GAME_QR_CODE_IMG_COS_PATH = "/lucky-cat/mini-game/qr-code-img";

    public List<WxMiniGame> getWxMiniGameList(){
        return wxMiniGameMapper.selectAll();
    }

    public List<Integer> getWxMiniGameIds(){
        List<WxMiniGame> wxMiniGameList = this.getWxMiniGameList();
        List<Integer> ids = wxMiniGameList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        return ids;
    }

    public void save(MultipartFile headImg,MultipartFile qrCodeImg,String nickname){

        //uploadFileService
    }

    public void delete(){

    }
}
