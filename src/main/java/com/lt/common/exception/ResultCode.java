package com.lt.common.exception;

public enum ResultCode {
    SUCCESS(200,"成功"),
    FAIL(500,"服务器内部错误");

    private int code;
    private String val;

    ResultCode(int code,String val) {
        this.code = code;
        this.val = val;
    }

    public int getCode() {
        return code;
    }

    public String getVal() {
        return val;
    }

}
