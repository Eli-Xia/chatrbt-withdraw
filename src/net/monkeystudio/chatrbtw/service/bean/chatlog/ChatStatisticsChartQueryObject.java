package net.monkeystudio.chatrbtw.service.bean.chatlog;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author xiaxin
 * @date 2018/1/17 15:58
 * 报表查询条件对象
 */
public class ChatStatisticsChartQueryObject {

    private Date beginDate;

    private Date endDate;

    public Date getBeginDate() {
        return beginDate;
    }

    //将"yyyy-MM-dd"类型的时间字符串注入到Date中
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
