package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.admin.controller.req.moneywithdraw.AdminMoneyWithdrawDetail;
import net.monkeystudio.admin.controller.req.moneywithdraw.InvoiceAuditReq;
import net.monkeystudio.admin.controller.req.moneywithdraw.RemitConfirmReq;
import net.monkeystudio.base.controller.bean.req.ListPaginationReq;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.service.UserService;
import net.monkeystudio.base.utils.FloatArithmeticUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.AccountSetting;
import net.monkeystudio.chatrbtw.entity.MoneyWithdrawRecord;
import net.monkeystudio.chatrbtw.entity.User;
import net.monkeystudio.chatrbtw.mapper.GlobalConfigMapper;
import net.monkeystudio.chatrbtw.mapper.MoneyWithdrawRecordMapper;
import net.monkeystudio.chatrbtw.service.bean.moneywithdraw.MoneyWithdrawIncomeMgrResp;
import net.monkeystudio.chatrbtw.service.bean.moneywithdraw.MoneyWithdrawRecordListResp;
import net.monkeystudio.portal.controller.resp.moneywithdraw.MoneyWithdrawDetail;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author xiaxin
 */
@Service
public class MoneyWithdrawService {

    //提现进度  1,2:提现进行中  3:提现成功
    private final static int MONEY_WITHDRAW_INVOICE_MAILING = 1;//发票邮寄
    private final static int MONEY_WITHDRAW_INVOICE_RECEIVED = 2;//收到发票
    private final static int MONEY_WITHDRAW_REMIT_FINISH = 3;//打款完成
    private final static String MONEY_WITHDRAW_SWITCH_OFF = "0";//提现开关(关闭)
    private final static String MONEY_WITHDRAW_SWITCH_ON = "1";//提现开关(开启)
    private final static Integer INVOICE_AUDIT_STATE_TRUE = 1;//发票审核正确状态
    private final static Integer INVOICE_AUDIT_STATE_FALSE = 0;//发票审核错误状态
    private final static Integer PERSONAL_ACCOUNT_TYPE = 1;	    //个人账户
    private final static Integer COMPANY_ACCOUNT_TYPE = 0;	    //公司账户

    @Autowired
    private MoneyWithdrawRecordMapper moneyWithdrawRecordMapper;

    @Autowired
    private GlobalConfigMapper globalConfigMapper;

    @Autowired
    private IncomeSerivce incomeSerivce;

    @Autowired
    private CfgService cfgService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountSettingService accountSettingService;

    @Autowired
    private COSService cosService;
    /**
     * 根据用户id获取提现记录
     * @param userId
     * @return
     */
    private List<MoneyWithdrawRecord> getMoneyWithdrawRecordList(Integer userId){
        Map<String,Object> param = new HashMap<>();
        param.put("userId",userId);
        return moneyWithdrawRecordMapper.selectByParamMap(param);
    }

    public Integer getCount(){
        return moneyWithdrawRecordMapper.count();
    }

    /**
     * 获取用户已提现的金额
     * @param userId
     * @return
     */
    private Float getAlreadyWithdrawAmount(Integer userId){
        List<Integer> states = Arrays.asList(MONEY_WITHDRAW_REMIT_FINISH);
        return moneyWithdrawRecordMapper.sumByState(states,userId);
    }

    /**
     * 获取用户提现进行中的金额
     * @param userId
     * @return
     */
    private Float getApplyingAmount(Integer userId){
        List<Integer> states = Arrays.asList(MONEY_WITHDRAW_INVOICE_MAILING,MONEY_WITHDRAW_INVOICE_RECEIVED);
        return moneyWithdrawRecordMapper.sumByState(states,userId);
    }

    /**
     * 获取用户可提现金额
     * @param userId
     * @return
     */
    public Float getAvailableAmount(Integer userId) throws BizException {
        //可提现金额  =  总收益 - 已提现金额 - 提现申请中金额
        BigDecimal historyTotalIncome = incomeSerivce.getHistoryTotalIncome(userId);

        Float minWithdrawAmountFloat = cfgService.getFloat(GlobalConfigConstants.MONEY_WITHDRAW_MIN_AMOUNT);

        BigDecimal minWithdrawAmountBd = FloatArithmeticUtil.float2BigDecimal(minWithdrawAmountFloat);

        //当总收益小于最小提现金额时,直接返回现有的总收益额,前端提现按钮失效.
        if(historyTotalIncome.compareTo(minWithdrawAmountBd) <= 0){
            return historyTotalIncome.floatValue();
        }

        BigDecimal amount = FloatArithmeticUtil.add(this.getApplyingAmount(userId), this.getAlreadyWithdrawAmount(userId));

        BigDecimal availableAmount = historyTotalIncome.subtract(amount);

        return availableAmount.floatValue();
    }

    public MoneyWithdrawIncomeMgrResp getIncomeMgrResp(Integer userId) throws BizException {

        MoneyWithdrawIncomeMgrResp resp = new MoneyWithdrawIncomeMgrResp();

        resp.setTotalAmount(incomeSerivce.getHistoryTotalIncome(userId).floatValue());

        resp.setAvailableAmount(this.getAvailableAmount(userId));

        resp.setAlreadyWithdrawAmount(this.getAlreadyWithdrawAmount(userId));

        resp.setMoneyWithdrawRecordList(this.getMoneyWithdrawRecordList(userId));

        resp.setSwitchVal(this.cfgService.getInteger(GlobalConfigConstants.MONEY_WITHDRAW_SWITCH));

        resp.setMinAmount(cfgService.getFloat(GlobalConfigConstants.MONEY_WITHDRAW_MIN_AMOUNT));

        return resp;
    }


    /*//是否在规定时间内进行提现,每月2~4号.
     public boolean isInLimitTime(Date applyTime){

        int day = DateUtils.getDayOfMonth(applyTime);

        return day >= AppConstants.MONEY_WITHDRAW_APPLY_BEGIN_TIME && day <= AppConstants.MONEY_WITHDRAW_APPLY_END_TIME;
    }*/

     //提现金额至少1000
     public boolean isMoreThanMinAmount(Float applyAmount){
         return FloatArithmeticUtil.compare2FloatArgs(applyAmount,cfgService.getFloat(GlobalConfigConstants.MONEY_WITHDRAW_MIN_AMOUNT)) >= 0;
     }

     //提现金额至多为可提现金额
     public boolean isLessThanAvailableAmount(Float applyAmount,Integer userId) throws BizException {
         return FloatArithmeticUtil.compare2FloatArgs(applyAmount,this.getAvailableAmount(userId)) <= 0;
     }

     //提现申请
     public void apply(Float amount,Integer userId) throws BizException{

        MoneyWithdrawRecord record = new MoneyWithdrawRecord();

        record.setUserId(userId);

        record.setState(MONEY_WITHDRAW_INVOICE_MAILING);

        record.setApplyTime(new Date());

        record.setAmount(amount);

        moneyWithdrawRecordMapper.insert(record);
     }

     //提现记录查看详情
     public MoneyWithdrawDetail getMoneyWithdrawDetail(Integer id){
         MoneyWithdrawRecord record = moneyWithdrawRecordMapper.selectByPrimaryKey(id);

         MoneyWithdrawDetail detail = new MoneyWithdrawDetail();

         detail.setApplyAmount(record.getAmount());

         detail.setApplyTime(record.getApplyTime());

         detail.setRemitTime(record.getSuccessTime());

         detail.setState(record.getState());

         return detail;
     }


    public List<MoneyWithdrawRecordListResp> listPaginationRecord(ListPaginationReq paginationReq){
        List<MoneyWithdrawRecord> list = moneyWithdrawRecordMapper.selectByPage(paginationReq.getStartIndex(), paginationReq.getPageSize());

        List<MoneyWithdrawRecordListResp> records = new ArrayList<>();

        for(MoneyWithdrawRecord record:list){

            MoneyWithdrawRecordListResp resp = new MoneyWithdrawRecordListResp();

            BeanUtils.copyProperties(record,resp);

            Map<Integer,User> cacheMap = new HashMap<>();//提现记录中有不同的用户,用map缓存避免同一个用户多次db查询
            Integer userId = record.getUserId();

            User user = null;

            try{
                user = cacheMap.get(userId);
                if(user == null){
                    user = userService.getUser(userId);

                    cacheMap.put(userId,user);
                }
            }catch(Exception e){
                Log.e(e);
            }
            if(user != null){
                resp.setUsername(user.getUsername());

                resp.setNickname(user.getNickname());
            }

            records.add(resp);
        }
        return records;
    }

    public MoneyWithdrawRecord getRecordById(Integer id) {
        return moneyWithdrawRecordMapper.selectByPrimaryKey(id);
    }

    //发票审核
    public void invoiceAudit(InvoiceAuditReq req){
        //无论审核结果正确或错误,提现记录状态都修改为收到发票状态
        MoneyWithdrawRecord record = new MoneyWithdrawRecord();

        record.setId(req.getMoneyWithdrawRecordId());

        record.setInvoiceReceiveTime(new Date());

        record.setInvoiceAuditRet(req.getRet());

        record.setInvoiceNumber(req.getInvoiceNumber());

        record.setState(MONEY_WITHDRAW_INVOICE_RECEIVED);

        moneyWithdrawRecordMapper.updateByPrimaryKeySelective(record);
    }

    public void remitConfirm(RemitConfirmReq req) throws BizException {
        MoneyWithdrawRecord dbRecord = this.moneyWithdrawRecordMapper.selectByPrimaryKey(req.getMoneyWithdrawRecordId());

        if(StringUtils.isBlank(dbRecord.getInvoiceNumber()) || dbRecord.getInvoiceAuditRet() ==  INVOICE_AUDIT_STATE_FALSE ){
            throw new BizException("发票审核未完成!");
        }

        MoneyWithdrawRecord record = new MoneyWithdrawRecord();

        record.setId(req.getMoneyWithdrawRecordId());

        record.setSuccessTime(new Date());

        record.setBankFlowNumber(req.getBankFlowNumber());

        record.setState(MONEY_WITHDRAW_REMIT_FINISH);

        moneyWithdrawRecordMapper.updateByPrimaryKeySelective(record);
    }

    //获取提现开关值
    public Map getMoneyWithdrawSwitch(){
        Map<String,Object> jsonMap = new HashMap<>();

        Integer val = cfgService.getInteger(GlobalConfigConstants.MONEY_WITHDRAW_SWITCH);

        jsonMap.put("switchVal",val);

        return jsonMap;
    }

    //修改提现开关
    public void changeSwitch(){

        String switchVal = cfgService.get(GlobalConfigConstants.MONEY_WITHDRAW_SWITCH);

        switchVal = switchVal.trim().equals(MONEY_WITHDRAW_SWITCH_ON)?MONEY_WITHDRAW_SWITCH_OFF:MONEY_WITHDRAW_SWITCH_ON;

        cfgService.set(GlobalConfigConstants.MONEY_WITHDRAW_SWITCH,switchVal);

    }

    //判断提现开关是否开启
    public boolean isSwitchOpen(){
        String switchVal = cfgService.get(GlobalConfigConstants.MONEY_WITHDRAW_SWITCH);
        return MONEY_WITHDRAW_SWITCH_ON.equals(switchVal);
    }

    public AdminMoneyWithdrawDetail getAdminRecordDetailById(Integer id) {

        MoneyWithdrawRecord record = this.getRecordById(id);

        AccountSetting as = accountSettingService.getAccountSettingByUserId(record.getUserId());

        AdminMoneyWithdrawDetail detail = new AdminMoneyWithdrawDetail();

        detail.setBankFlowNumber(record.getBankFlowNumber());
        detail.setAccountNumber(as.getAccountNumber());
        detail.setAccountType(as.getAccountType());
        detail.setAmount(record.getAmount());
        detail.setBankForkName(as.getBankForkName());
        detail.setBankName(as.getBankName());
        detail.setEmail(as.getEmail());
        detail.setInvoiceNumber(record.getInvoiceNumber());
        detail.setInvoiceRet(record.getInvoiceAuditRet());
        detail.setLocation(as.getProvince()+as.getCity());
        detail.setAccountName(as.getAccountName());

        if(as.getAccountType() == PERSONAL_ACCOUNT_TYPE){
            detail.setAccountHolder(as.getAccountHolder());
            detail.setIdNumber(as.getIdNumber());
        }
        User user = null;
        try{
            user = userService.getUser(record.getUserId());
        }catch(Exception e){
            Log.e(e);
        }
        if(user != null){
            detail.setNickname(user.getNickname());
        }
        return detail;
    }

    //前端预览用户委托个人收款证明(账户为个人类型)
    public void getCertificationPic(Integer id, HttpServletResponse response) {

        MoneyWithdrawRecord record = this.getRecordById(id);

        Integer wxPubOwnerId = record.getUserId();

        AccountSetting setting = accountSettingService.getAccountSettingByUserId(wxPubOwnerId);

        if(accountSettingService.isPersonalAccountType(setting.getAccountType())){

            accountSettingService.handleSensitivePic(wxPubOwnerId,response);
        }
    }


}
