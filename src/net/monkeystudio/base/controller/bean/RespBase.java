package net.monkeystudio.base.controller.bean;


import net.monkeystudio.base.controller.JsonSupportEntity;
import net.monkeystudio.base.utils.JsonHelper;

/**
 * 基础响应报文
 * @author hebo2
 *
 */
public class RespBase extends JsonSupportEntity
{
	private int retCode;		//返回码
	private String retMsg;		//错误消息
	private Integer total;      //用于列表，返回数据总数
	private Object result;
	
	public RespBase(){};
	
	public RespBase(Integer retCode){
		this.retCode = retCode;
		this.retMsg = RetCode.getMsg(retCode);
	}
	
	public RespBase(Integer retCode, String retMsg){
		this.retCode = retCode;
		this.retMsg = retMsg;
	}
	
	public int getRetCode() 
	{
		return retCode;
	}
	
	public String getRetMsg()
	{
		return retMsg;
	}

	@Override
	public String toJsonString() 
	{
		return JsonHelper.bean2json(this);
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}


	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}


	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}


	public Integer getTotal() {
		return total;
	}


	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
}
