package com.springboot.hbase.mapper;

import java.util.HashMap;
import java.util.Map;

public enum FeedbackType {
    PASS(1, "feedback for coupon"),
    APP(2, "feedback for app");

    private Integer code;
    private String desc;

    FeedbackType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
        MapHolder.mapping.put(this.code, this);
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static FeedbackType getFeedbackType(int id) {
        return MapHolder.mapping.get(id);
    }

    private static class MapHolder {
        public final static Map<Integer, FeedbackType> mapping = new HashMap<>();
    }
}
