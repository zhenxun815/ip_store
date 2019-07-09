package com.tqhy.ip_store.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
public class DateUtil {

    private static final String DEFAULT_PATTERN = "yyyyMMdd";

    public static Date parseDateStr(String pattern, String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern(pattern);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseDateStr(String dateStr) {
        return parseDateStr(DEFAULT_PATTERN, dateStr);
    }

    public static String formatDate(String pattern, Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(pattern);
        String format = sdf.format(date);
        return format;
    }

    public static String formatDate(Date date) {
        return formatDate(DEFAULT_PATTERN, date);
    }
}
