package com.winter.flashsale.vo;

import lombok.Data;


@Data
public class FlashSaleGoodsVO {

    /**
     * 活动id
     */
    private Long promotionId;
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
    private Long flashSaleLimit;
    /**
     * 排序
     */
    private Integer flashSaleSort;
}
