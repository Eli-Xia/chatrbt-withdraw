package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.admin.controller.req.res.AddUpdateRes;
import net.monkeystudio.admin.controller.resp.ResForResp;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.Res;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.mapper.ResMapper;
import net.monkeystudio.utils.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class PermissionService {

    @Autowired
    private ResMapper resMapper;

    @Autowired
    private UserRoleService userRoleService;


    public List<ResForResp> getRes(Integer page, Integer pageSize) {

        Integer startIndex = CommonUtils.page2startIndex(page, pageSize);

        List<Res> resList = resMapper.selectByPage(startIndex, pageSize);

        List<Role> roles = userRoleService.getRoleList();

        List<ResForResp> resForResps = new ArrayList<>();

        for(Res res:resList){
            ResForResp resForResp = new ResForResp();

            BeanUtils.copyProperties(res,resForResp,"allowRoles");

            List<String> roleList = CommonUtils.splitString2List(res.getAllowRoles(),",");

            List<String> roleNameList = this.alterRoleCodeToName(roles, roleList);

            resForResp.setAllowRoles(roleNameList);

            resForResps.add(resForResp);
        }

        return resForResps;
    }
    //List<role.code>->List<role.name>
    private List<String> alterRoleCodeToName(List<Role> roles,List<String> roleCodeList){
        List<String> roleNameList = new ArrayList<>();
        for (String code:roleCodeList){
            for(Role role:roles){
                if(role.getCode().equals(code)){
                    roleNameList.add(role.getName());
                    break;
                }
            }
        }
        return roleNameList;
    }

    public Integer getCount() {
        return resMapper.count();
    }

    /**
     * ["role1","role2"] ==> "role1,role2"
     * @param roleList
     * @return
     */
    private String roleArrToRoleStr(List<String> roleList){
        StringBuilder sb = new StringBuilder();
        if(ListUtil.isEmpty(roleList)){
            return "";
        }
        for (String role:roleList){
            if(!StringUtils.isEmpty(role) && role.equals(roleList.get(0))){//如果index为0,不需要","
                sb.append(role);
            }else{
                sb.append(",");
                sb.append(role);
            }
        }
        return sb.toString();
    }

    public void update(Integer id, AddUpdateRes addUpdateRes) {

        List<String> allowRoles = addUpdateRes.getAllowRoles();

        addUpdateRes.setId(id);

        Res res = new Res();

        BeanUtils.copyProperties(addUpdateRes,res,"allowRoles");

        res.setAllowRoles(this.roleArrToRoleStr(allowRoles));

        resMapper.update(res);
    }

    public void save(AddUpdateRes addUpdateRes) {

        List<String> allowRoles = addUpdateRes.getAllowRoles();

        Res res = new Res();

        BeanUtils.copyProperties(addUpdateRes,res,"allowRoles");

        Log.i("name=[?],res=[?],desc=[?],allowRoles=[?]",res.getName(),res.getRes(),res.getDescription(),res.getAllowRoles());

        res.setAllowRoles(this.roleArrToRoleStr(allowRoles));

        resMapper.insert(res);
    }

    public void delete(Integer id) {
        resMapper.delete(id);
    }

    public ResForResp getResById(Integer id) {
        Res res = resMapper.selectByPrimaryKey(id);
        ResForResp resForResp = new ResForResp();
        BeanUtils.copyProperties(res,resForResp,"allowRoles");
        resForResp.setAllowRoles(CommonUtils.splitString2List(res.getAllowRoles(),","));
        return resForResp;
    }

    //shiro对res做了限制
    public boolean checkoutRes(String res){
        return !StringUtils.isEmpty(res) && !isKeyValueSeparatorChar(res.charAt(0));
    }

    private static boolean isKeyValueSeparatorChar(char c) {
        return Character.isWhitespace(c) || c == ':' || c == '=';
    }

}
