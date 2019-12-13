package me.zjl.boot.model;

public enum ResponseCode {

    SUCCESS(200, "成功"),

    LOST(401, "用户未登录"),

    TIP_ERROR(406, "请替换为当前场景的提示内容"),

    UNKNOWN_ERROR(500, "未知错误");

    private Integer code;

    private String message;

    ResponseCode(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer code(){
        return code;
    }

    public String message(){
        return message;
    }
}
