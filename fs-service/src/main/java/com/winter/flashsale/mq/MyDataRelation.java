package com.winter.flashsale.mq;

import java.util.HashMap;
import java.util.Map;

public class MyDataRelation {

    public static final Map<String, String> map = new HashMap<>();

    public static void add(String id, String value) {
        map.put(id, value);
    }

    public static String get(String id) {
        return map.get(id);
    }

    public static void del(String id) {
        map.remove(id);
    }
}
