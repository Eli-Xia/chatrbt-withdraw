package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.chatrbtw.entity.CryptoKitties;
import net.monkeystudio.chatrbtw.mapper.CryptoKittiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bint on 2018/4/21.
 */
@Service
public class CryptoKittiesService {

    @Autowired
    private CryptoKittiesMapper cryptoKittiesMapper;

    @Autowired
    private CfgService cfgService;


    /**
     * 派发以太猫
     * @param wxPubOriginId
     * @param wxFanOpenId
     */
    public void designateKitty(String wxPubOriginId ,String wxFanOpenId){
        cryptoKittiesMapper.updateMinItem(wxPubOriginId, wxFanOpenId);
    }


    /**
     * 获取以太猫
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @return
     */
    public CryptoKitties getKittyByOwner(String wxPubOriginId , String wxFanOpenId){
        CryptoKitties cryptoKitties = this.getKittyByOwnerFromDb(wxPubOriginId,wxFanOpenId);

        String url = cryptoKitties.getUrl();
        String newUrl = this.transformUrl(url);

        cryptoKitties.setUrl(newUrl);

        return cryptoKitties;
    }

    private CryptoKitties getKittyByOwnerFromDb(String wxPubOriginId , String wxFanOpenId){
        return cryptoKittiesMapper.selectByWxFan(wxPubOriginId,wxFanOpenId);
    }

    public List<String> getAllUrl(Integer startIndex,Integer pageSize){
        return this.cryptoKittiesMapper.selectAllUrl(startIndex,pageSize);
    }

    private String transformUrl(String url){

        Integer index = url.lastIndexOf("/");
        String fileName = url.substring(index + 1, url.length());

        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        return "https://" + domain +"/crypto-kitty/" + fileName;

    }

    public List<String> getAll(){
        return this.cryptoKittiesMapper.selectAll();
    }
}
