package com.winter.flashsale.controller;

import com.alibaba.fastjson.JSONObject;
import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.model.ApiResponse;
import com.winter.common.model.Status;
import com.winter.flashsale.service.impl.FlashSaleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/flashsale")
public class FlashSaleController {

    private final FlashSaleServiceImpl flashSaleServiceImpl;

    public FlashSaleController(FlashSaleServiceImpl flashSaleServiceImpl) {
        this.flashSaleServiceImpl = flashSaleServiceImpl;
    }

    @PostMapping("/flashSale/{sessionId}/{goodsId}/{ramdom}/{userId}/{num}")
    public JSONObject flashSale(@PathVariable("sessionId") String sessionId,
                                @PathVariable("goodsId") String goodsId,
                                @PathVariable("ramdom") String random,
                                @PathVariable("userId") String userId,
                                @PathVariable("num") Integer num) {
        ApiResponse result = flashSaleServiceImpl.doFlashSale(sessionId, goodsId, random, userId, num);

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
                response.put("goods_num", orderMessage.getGoodsNum());
                response.put("session_id", orderMessage.getSessionId());
            }
        }

        return response;
    }
}
