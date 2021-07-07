package com.winter.flashsale.to;

import lombok.Data;


@Data
public class FlashSaleGoodsRedTO {

    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 秒杀价格
     */
    private Long flashSalePrice;
    /**
     * 秒杀总量
     */
    private Long flashSaleCount;
    /**
     * 每人限购数量
     */
    private Long purchaseLimit;
    /**
     * 排序
     */
    private Integer flashSaleSort;

    private Long startTime;
    private Long endTime;

    // 秒杀随机码
    private String randomCode;

    // TODO Goods Info VO
    // private SkuInfoVO skuInfo;

}
