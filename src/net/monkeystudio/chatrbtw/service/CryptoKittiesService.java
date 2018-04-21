package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.CryptoKitties;
import net.monkeystudio.chatrbtw.mapper.CryptoKittiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/4/21.
 */
@Service
public class CryptoKittiesService {

    @Autowired
    private CryptoKittiesMapper cryptoKittiesMapper;

    public void designateKitty(String wxPubOriginId ,String wxFanOpenId){
        cryptoKittiesMapper.updateMinItem(wxPubOriginId, wxFanOpenId);


    }

    public CryptoKitties getKittyByOwner(String wxPubOriginId , String wxFanOpenId){
        return cryptoKittiesMapper.selectByWxFan(wxPubOriginId,wxFanOpenId);
    }

}
