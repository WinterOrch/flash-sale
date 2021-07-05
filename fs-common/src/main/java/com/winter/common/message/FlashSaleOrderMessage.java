package com.winter.common.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlashSaleOrderMessage {

    private Long orderId;

    private String userId;

    private String goodsId;

    public Long getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getGoodsId() {
        return goodsId;
    }
}
