package com.winter.flashsale.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 一次秒杀活动信息的VO
 *      秒杀活动持续时间有限，
 *      且可以包含多项秒杀商品
 * Created in 10:18 2021/7/6
 */
@Data
public class FlashSaleSessionVO {

    private Long id;
    private String name;

    private Date startTime;
    private Date endTime;

    private Date createTime;
    private Integer status;

    private List<FlashSaleGoodsVO> relatedGoods;

}
