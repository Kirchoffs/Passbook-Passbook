package com.syh.passbook.passbook.constant;

public enum FeedbackType {
    PASS(1, "feedback for coupon"),
    APP(2, "feedback for App");

    private Integer code;
    private String desc;

    FeedbackType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
