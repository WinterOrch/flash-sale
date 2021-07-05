package com.winter.flashsale.controller;

import com.alibaba.fastjson.JSONObject;
import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.model.ApiResponse;
import com.winter.common.model.Status;
import com.winter.flashsale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/flashsale")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    public FlashSaleController(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @PostMapping("/flashSale/{goodsId}/{userId}")
    public JSONObject flashSale(@PathVariable("goodsId") Long goodsId,
                                @PathVariable("userId") Long userId) {
        String strGoodsId = "" + goodsId;
        String strUserId = "" + userId;

        ApiResponse result = flashSaleService.doFlashSale(strGoodsId, strUserId);

        JSONObject response = new JSONObject();
        response.put("code", result.getCode());

        if (result.getCode().equals(Status.OK.getCode())) {
            if (result.getResult() == null) {
                log.error("Flash Sale Service Returned Success without Order Message!");
            } else {
                FlashSaleOrderMessage orderMessage = (FlashSaleOrderMessage) result.getResult();
                response.put("order_id", orderMessage.getOrderId());
                response.put("buyer_id", orderMessage.getUserId());
                response.put("goods_id", orderMessage.getGoodsId());
            }
        }

        return response;
    }
}
