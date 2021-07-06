package com.winter.flashsale.feign.impl;

import com.winter.common.model.ApiResponse;
import com.winter.flashsale.feign.CouponFeign;
import com.winter.flashsale.vo.FlashSaleGoodsVO;
import com.winter.flashsale.vo.FlashSaleSessionVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (仅供测试用) Feign 接口实现类
 */
@Service
public class CouponFeignImpl implements CouponFeign {

    private boolean sent;

    public CouponFeignImpl() {
        this.sent = false;
    }

    @Override
    public ApiResponse getLatestFlashSaleSession() {
        if (this.sent) {
            return ApiResponse.ofMessage("No More");
        }

        List<FlashSaleSessionVO> sessionVOList = new ArrayList<>();

        FlashSaleSessionVO sessionVO = new FlashSaleSessionVO();
        sessionVO.setId(1L);
        sessionVO.setName("TEST");
        sessionVO.setCreateTime(new Date());
        sessionVO.setStartTime(Date.from(startTime().toInstant(ZoneOffset.of("+8"))));
        sessionVO.setEndTime(Date.from(endTime().toInstant(ZoneOffset.of("+8"))));
        sessionVO.setStatus(1);

        List<FlashSaleGoodsVO> goods = new ArrayList<>();

        FlashSaleGoodsVO goodsVO = new FlashSaleGoodsVO();
        goodsVO.setFlashSaleCount(200);
        goodsVO.setGoodsId(1L);
        goodsVO.setFlashSaleLimit(1L);
        goodsVO.setFlashSaleSort(1);
        goodsVO.setPromotionId(666L);
        goodsVO.setId(22L);
        goodsVO.setFlashSalePrice(998L);
        goodsVO.setPromotionSessionId(1L);
        goods.add(goodsVO);

        sessionVO.setRelatedGoods(goods);
        sessionVOList.add(sessionVO);

        sent = true;

        return ApiResponse.ofSuccess("Has Session", sessionVOList);
    }

    private LocalDateTime startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;

        return LocalDateTime.of(now, min);
    }

    private LocalDateTime endTime() {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);

        return LocalDateTime.of(localDate, LocalTime.MAX);
    }
}
