package com.winter.flashsale.service;

import com.winter.flashsale.consts.Prefix;
import com.winter.flashsale.redis.RedisService;
import com.winter.flashsale.zookeeper.ZkService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class FlashSaleService {

    private static final ConcurrentHashMap<String, Boolean> goodsCache = new ConcurrentHashMap<>();

    private final ZkService zkService;
    private final RedisService redService;

    public FlashSaleService(ZkService zkService, RedisService redisService) {
        this.zkService = zkService;
        this.redService = redisService;
    }

    public void doFlashSale(String strGoodsId) {

    }

    public void flashSaleFailed(String strGoodsId) {
        redService.increment(Prefix.RED_GOODS__KEY_PREFIX + strGoodsId);

        markFalseForGoods(strGoodsId);
    }

    public void markFalseForGoods(String strGoodsId) {
        if (goodsCache.get(strGoodsId) != null) {
            goodsCache.remove(strGoodsId);
        }

        zkService.setFalse(strGoodsId);
    }

    public static ConcurrentHashMap<String, Boolean> getGoodsCache() {
        return goodsCache;
    }
}
