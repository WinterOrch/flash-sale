package com.winter.flashsale.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlashSaleGoodsVO {

    private Long id;
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
    private Integer flashSaleCount;
    /**
     * 每人限购数量
     */
    private Long flashSaleLimit;
    /**
     * 排序
     */
    private Integer flashSaleSort;
}
