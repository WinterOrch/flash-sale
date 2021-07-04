package com.winter.flashsale.controller;

import com.alibaba.fastjson.JSONObject;
import com.winter.flashsale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/flashsale")
public class FlashSaleController {

    private FlashSaleService flashSaleService;

    public FlashSaleController(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @PostMapping("/flashSale/{goodsId}/{userId}")
    public JSONObject flashSale(@PathVariable("goodsId") Long goodsId,
                                @PathVariable("userId") Long userId) {
        String cacheKey = "" + goodsId;

        if (FlashSaleService.getGoodsCache().containsKey(cacheKey)) {

        }
    }
}
