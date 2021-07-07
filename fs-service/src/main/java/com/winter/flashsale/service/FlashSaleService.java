package com.winter.flashsale.service;

import com.winter.common.model.ApiResponse;

import java.util.concurrent.ConcurrentHashMap;

public interface FlashSaleService {

    void uploadFlashSaleSession();

    ApiResponse doFlashSale(String sessionId ,String goodsId, String random, String userId, Integer num);

    static ConcurrentHashMap<String, Boolean> getGoodsCache() {
        return null;
    };

    void clearUpSession(String sessionKey);
}
