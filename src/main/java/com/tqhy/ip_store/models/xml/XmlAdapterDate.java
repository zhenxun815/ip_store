package com.tqhy.ip_store.models.xml;

import com.tqhy.ip_store.utils.DateUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
public class XmlAdapterDate extends XmlAdapter<String, Date> {


    @Override
    public Date unmarshal(String dateStr) throws Exception {
        return DateUtil.parseDateStr(dateStr);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return DateUtil.formatDate(date);
    }
}
