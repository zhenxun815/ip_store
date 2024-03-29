package com.tqhy.ip_store.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Yiheng
 * @create 8/28/2019
 * @since 1.0.0
 */
public class StringUtils {

    /**
     * 使用指定分隔符拼接字符串
     *
     * @param objs
     * @param separator
     * @param func
     * @param <T>
     * @return
     */
    public static <T> String join(List<T> objs, String separator, Function<T, String> func) {
        if (null == objs || objs.size() == 0) {
            return "";
        }
        StringBuilder builder = objs.stream()
                                    .collect(StringBuilder::new,
                                             (bd, obj) -> bd.append(func.apply(obj)).append(separator),
                                             StringBuilder::append);

        return builder.deleteCharAt(builder.lastIndexOf(separator)).toString();
    }

    /**
     * 拼接字符串
     *
     * @param strs
     * @return
     */
    public static String join(String... strs) {
        if (null == strs || strs.length == 0) {
            return "";
        }

        return Arrays.stream(strs)
                     .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                     .toString();
    }
}
