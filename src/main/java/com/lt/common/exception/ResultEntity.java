package com.lt.common.exception;

public class ResultEntity<T> {
    private int code;
    private String msg;
    private T data;

    public ResultEntity(){}
    public ResultEntity(int code,String msg){
        this(code,msg,null);
    }
    public ResultEntity(int code,String msg,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResultEntity<T> success(){
        return new ResultEntity<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getVal());
    }

    public static <T> ResultEntity<T> success(T data){
        return new ResultEntity<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getVal(),data);
    }

    public static <T> ResultEntity<T> fail(int code,String msg){
        return new ResultEntity<T>(code,msg);
    }

    public static <T> ResultEntity<T> fail(){
        return new ResultEntity<T>(ResultCode.FAIL.getCode(),ResultCode.FAIL.getVal());
    }
}
