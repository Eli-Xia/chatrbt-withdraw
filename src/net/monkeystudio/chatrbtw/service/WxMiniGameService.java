package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import net.monkeystudio.chatrbtw.mapper.WxMiniGameMapper;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameAdd;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameResp;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameUpdate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    private final static String WX_MINI_GAME_HEAD_IMG_COS_PATH = "/lucky-cat/mini-game/head-img";
    private final static String WX_MINI_GAME_QR_CODE_IMG_COS_PATH = "/lucky-cat/mini-game/qr-code-img";

    private final static Integer WX_MINI_GAME_SHELVE_STATE = 1;//小游戏上架状态
    private final static Integer WX_MINI_GAME_UNSHELVE_STATE = 0;//小游戏下架状态

    public final static Integer WX_MINI_GAME_NO_SIGN = 0;//小游戏没有new
    public final static Integer WX_MINI_GAME_NEED_SIGN = 1;//小游戏有new

    public List<WxMiniGame> getWxMiniGameList(){
        return wxMiniGameMapper.selectAll();
    }

    /**
     * 获取已上线,上架小游戏id list
     * @return
     */
    public List<Integer> getWxMiniGameIds(){
        List<WxMiniGame> wxMiniGameList = this.getMiniGameInfoList();
        List<Integer> ids = wxMiniGameList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        return ids;
    }

    public void save(AdminMiniGameAdd adminMiniGameAdd){

        MultipartFile headImg = adminMiniGameAdd.getHeadImg();
        MultipartFile qrCodeImg = adminMiniGameAdd.getQrCodeImg();

        String headImgFileName = headImg.getOriginalFilename();
        String qrCodeImgFileName = qrCodeImg.getOriginalFilename();

        String headImgUploadUrl= uploadService.uploadPic(headImg, WX_MINI_GAME_HEAD_IMG_COS_PATH, headImgFileName);
        String qrCodeImgUploadUrl = uploadService.uploadPic(qrCodeImg, WX_MINI_GAME_QR_CODE_IMG_COS_PATH, qrCodeImgFileName);

        WxMiniGame wxMiniGame = new WxMiniGame();

        BeanUtils.copyProperties(adminMiniGameAdd,wxMiniGame);
        wxMiniGame.setHeadImgUrl(headImgUploadUrl);
        wxMiniGame.setQrCodeImgUrl(qrCodeImgUploadUrl);
        wxMiniGame.setShelveState(WX_MINI_GAME_SHELVE_STATE);//默认为上架状态
        wxMiniGame.setCreateTime(new Date());

        wxMiniGameMapper.insert(wxMiniGame);
    }

    public void delete(Integer wxMiniGameId){
        wxMiniGameMapper.delete(wxMiniGameId);
    }

    public void update(AdminMiniGameUpdate adminMiniGameUpdate){
        WxMiniGame wxMiniGameById = this.getById(adminMiniGameUpdate.getId());

        BeanUtils.copyProperties(adminMiniGameUpdate,wxMiniGameById);

        MultipartFile headImg = adminMiniGameUpdate.getHeadImg();
        MultipartFile qrCodeImg = adminMiniGameUpdate.getQrCodeImg();

        if(headImg != null){
            String headImgFileName = headImg.getOriginalFilename();
            String headImgUploadUrl= uploadService.uploadPic(headImg, WX_MINI_GAME_HEAD_IMG_COS_PATH, headImgFileName);
            wxMiniGameById.setHeadImgUrl(headImgUploadUrl);

        }
        if(qrCodeImg != null){
            String qrCodeImgFileName = qrCodeImg.getOriginalFilename();
            String qrCodeImgUploadUrl = uploadService.uploadPic(qrCodeImg, WX_MINI_GAME_QR_CODE_IMG_COS_PATH, qrCodeImgFileName);
            wxMiniGameById.setQrCodeImgUrl(qrCodeImgUploadUrl);

        }

        wxMiniGameMapper.updateByPrimaryKey(wxMiniGameById);
    }

    public WxMiniGame getById(Integer miniGameId){
        return wxMiniGameMapper.selectByPrimaryKey(miniGameId);
    }

    /**
     * 小游戏下架
     * @param miniGameId
     */
    public void unshelve(Integer miniGameId){
        WxMiniGame wxMiniGame = this.getById(miniGameId);
        wxMiniGame.setShelveState(WX_MINI_GAME_UNSHELVE_STATE);
        wxMiniGameMapper.updateByPrimaryKey(wxMiniGame);
    }

    public List<AdminMiniGameResp> getAdminMiniGameRespList(){

        List<AdminMiniGameResp> resps = new ArrayList<>();

        List<WxMiniGame> wxMiniGames = wxMiniGameMapper.selectAll();

        for (WxMiniGame item:wxMiniGames){
            AdminMiniGameResp resp = new AdminMiniGameResp();
            BeanUtils.copyProperties(item,resp);
            //查询这个游戏历史完成任务的数量
            Long miniGamePlayerNum = chatPetMissionPoolService.getMiniGamePlayerNum(item.getId());
            resp.setPlayerNum(miniGamePlayerNum);
            resps.add(resp);
        }

        return resps;
    }


    /**
     * 游戏中心展示列表
     * @return
     */
    public List<WxMiniGame> getMiniGameInfoList(){
        List<WxMiniGame> wxMiniGameList = this.getWxMiniGameList();

        Iterator<WxMiniGame> iterator = wxMiniGameList.iterator();

        while(iterator.hasNext()){
            WxMiniGame item = iterator.next();
            Long onlineTime = DateUtils.getBeginDate(item.getOnlineTime()).getTime();
            Long nowTime = new Date().getTime();
            Integer shelveState = item.getShelveState();

            //如果小游戏还未上线或者已经下架,则不展示
            if(nowTime.intValue() < onlineTime.intValue() || WX_MINI_GAME_UNSHELVE_STATE.equals(shelveState)){
                iterator.remove();
            }
        }
        return wxMiniGameList;

    }

    /**
     * 判断上线时间是否正确,
     * @param onlineTime
     * @return
     */
    public Boolean isOnlineTimeValid(Date onlineTime){
        Boolean ret = false;

        //上线当天0点
        Date online = DateUtils.getBeginDate(onlineTime);
        long onlineTimeStamp = online.getTime();

        //设置当天23点59分59秒
        Date todayEndTime = DateUtils.getEndDate(new Date());
        long todayTimeStamp = todayEndTime.getTime();

        if(onlineTimeStamp > todayTimeStamp){
            ret = true;
        }

        return ret;
    }
}
