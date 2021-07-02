package com.winter.flashsale.exphandler;

public enum  Status {
    OK(200, "操作成功"),
    UNKNOWN_ERROR(500, "服务器出错啦");

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 内容
     */
    private final String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
