package com.winter.flashsale.utils;

import com.alibaba.fastjson.JSON;

public class StringUtils {
    public static <T> String bean2String(T value) {
        if (value == null)
            return null;

        Class<?> clazz = value.getClass();

        if (clazz == Integer.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }
}
