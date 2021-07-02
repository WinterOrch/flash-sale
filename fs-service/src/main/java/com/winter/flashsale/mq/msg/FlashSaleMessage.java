package com.winter.flashsale.mq.msg;

import com.winter.flashsale.entity.FlashSaleUser;

public class FlashSaleMessage {

    private FlashSaleUser user;
    private long goodsId;

    public FlashSaleUser getUser() {
        return user;
    }

    public void setUser(FlashSaleUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
