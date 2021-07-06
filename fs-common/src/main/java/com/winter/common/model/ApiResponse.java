package com.winter.common.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winter.common.exception.AbstractException;

public class ApiResponse {
    private Integer code;
    private String message;

    private Object result;

    private ApiResponse() {
    }

    private ApiResponse(Integer code, String message, Object result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    /**
     * 构造一个自定义的API返回
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     * @return ApiResponse
     */
    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess(String message, Object data) {
        return of(Status.OK.getCode(), message, data);
    }

    /**
     * 构造一个自定义消息的API返回
     *
     * @param message 返回内容
     * @return ApiResponse
     */
    public static ApiResponse ofMessage(String message) {
        return of(Status.INFO.getCode(), message, null);
    }

    /**
     * 构造一个有状态的API返回
     *
     * @param status 状态 {@link Status}
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(Status status) {
        return ofStatus(status, null);
    }

    /**
     * 构造一个有状态且带数据的API返回
     *
     * @param status 状态 {@link Status}
     * @param data   返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(Status status, Object data) {
        return of(status.getCode(), status.getMessage(), data);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param t    异常
     * @param data 返回数据
     * @param <T>  {@link AbstractException} 的子类
     * @return ApiResponse
     */
    public static <T extends AbstractException> ApiResponse ofException(T t, Object data) {
        return of(t.getCode(), t.getMessage(), data);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param t   异常
     * @param <T> {@link AbstractException} 的子类
     * @return ApiResponse
     */
    public static <T extends AbstractException> ApiResponse ofException(T t) {
        return ofException(t, null);
    }

    public boolean isSuccessful() {
        return this.getCode().equals(Status.OK.getCode());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public <T> T getData(TypeReference<T> typeReference) {
        if (this.result == null) {
            return null;
        } else {
            return JSON.parseObject(JSON.toJSONString(this.result), typeReference);
        }
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
