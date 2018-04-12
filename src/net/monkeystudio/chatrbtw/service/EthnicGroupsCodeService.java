package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.EthnicGroupsCode;
import net.monkeystudio.chatrbtw.mapper.EthnicGroupsCodeMapper;
import net.monkeystudio.chatrbtw.service.bean.ethnicgroupscode.EthnicGroupsCodeValidatedResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/4/10.
 */
@Service
public class EthnicGroupsCodeService {

    @Autowired
    private EthnicGroupsCodeMapper ethnicGroupsCodeMapper;

    private final static Integer ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ENABLE = 0; //可用
    private final static Integer ETHNIC_GROUPS_CODE_VALIDATED_STATUS_NOT_FOUND = 1; //找不到族群码
    private final static Integer ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ACOUNT_FULL = 2; //族群码满额

    /**
     * 获取族群码
     * @param code
     * @param wxPubOriginId
     * @return
     */
    public EthnicGroupsCode getByWxPubAndCode(String code , String wxPubOriginId){
        return ethnicGroupsCodeMapper.selectByWxPubAndCode(wxPubOriginId ,code);
    }

    private Integer save(EthnicGroupsCode ethnicGroupsCode) {

        return ethnicGroupsCodeMapper.insert(ethnicGroupsCode);
    }


    public EthnicGroupsCodeValidatedResp validated(String code , String wxPubOriginId , String wxFanOpendId){

        EthnicGroupsCode parentEthnicGroupsCode = this.getByWxPubAndCode(code, wxPubOriginId);

        EthnicGroupsCodeValidatedResp ethnicGroupsCodeValidatedResp = null;
        //不存在族群码
        if(parentEthnicGroupsCode == null){
            Integer status = ETHNIC_GROUPS_CODE_VALIDATED_STATUS_NOT_FOUND;
            String content = "找不到该族群码，请检查族群码";

            return new EthnicGroupsCodeValidatedResp(status,content);
        }

        //数量超过限制
        if(parentEthnicGroupsCode.getTotalValidCount() != null){
            Integer count = ethnicGroupsCodeMapper.countByCode(code);

            if(count.intValue() >= parentEthnicGroupsCode.getTotalValidCount().intValue() ){
                String content = "族群已满员，下次早点来哦！";

                return new EthnicGroupsCodeValidatedResp(ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ACOUNT_FULL,content);
            }
        }

        return new EthnicGroupsCodeValidatedResp(ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ENABLE, null);
    }

    //TODO
    private String getH5Url(String wxPubOriginId , String wxFanOpendId){

        


        return "";
    }

}
