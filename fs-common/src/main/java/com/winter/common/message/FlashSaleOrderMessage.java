package com.winter.common.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlashSaleOrderMessage {

    private Long orderId;

    private String sessionId;

    private String userId;

    private String goodsId;

    private Integer goodsNum;

    public Long getOrderId() {
        return orderId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }
}
