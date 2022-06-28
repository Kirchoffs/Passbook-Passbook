package com.syh.passbook.passbook.constant;

public enum PassStatus {
    UNUSED(1, "not used"),
    USED(2, "already used"),
    ALL(3, "all the received");

    private Integer code;
    private String desc;

    PassStatus(Integer code, String desc) {
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
