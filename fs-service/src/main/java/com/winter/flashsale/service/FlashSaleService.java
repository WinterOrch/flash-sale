package com.winter.flashsale.service;

import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.model.ApiResponse;
import com.winter.common.model.Status;
import com.winter.common.utils.uuid.SnowFlake;
import com.winter.flashsale.consts.Prefix;
import com.winter.flashsale.mq.Sender;
import com.winter.flashsale.redis.RedisService;
import com.winter.flashsale.zookeeper.ZkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class FlashSaleService {

    /**
     * JVM Cache for flash sale goods
    * */
    private static final ConcurrentHashMap<String, Boolean> goodsCache = new ConcurrentHashMap<>();

    private final ZkService zkService;
    private final RedisService redService;
    private final Sender sender;

    private final SnowFlake snowFlake;

    public FlashSaleService(ZkService zkService, RedisService redisService, Sender sender,
                            @Value("${snowflake.dcid}") long dataCentreId, @Value("${snowflake.machineid}") long machineId) {
        this.zkService = zkService;
        this.redService = redisService;
        this.sender = sender;

        this.snowFlake = new SnowFlake(dataCentreId, machineId);
    }

    /**
     * Response Messages for Flash Sale
     * */
    public static final String RESP_SOLD_OUT = "goods has been sold out";
    public static final String RESP_ORDER_CREATED = "order has been created";
    public static final String RESP_ORDER_EXISTS = "order already exists";
    public static final String STOCK_NOT_EXISTS = "what the hell";
    public static final String SOMETHING_WENT_WRONG = "please try again";

    /**
     * 秒杀过程的主要逻辑
     * created in 17:39 2021/7/5
     */
    public ApiResponse doFlashSale(String goodsId, String userId) {

        if (goodsCache.containsKey(goodsId)) {
            return ApiResponse.ofMessage(RESP_SOLD_OUT);
        }

        RedisService.RedResponse result = redService.redFlashSale(goodsId, userId);

        switch (result) {
            case STOCK_OUT:
                zkService.setSoldOutTrue(goodsId);
                return ApiResponse.ofMessage(RESP_SOLD_OUT);

            case NOT_IN_STOCK:
                return ApiResponse.ofMessage(STOCK_NOT_EXISTS);

            case ORDER_EXISTS:
                return ApiResponse.ofMessage(RESP_ORDER_EXISTS);

            case ORDER_CREATED:
                FlashSaleOrderMessage orderMessage = new FlashSaleOrderMessage(snowFlake.nextId(), userId, goodsId);
                sender.sendDirectFlashSaleOrder(orderMessage);
                return ApiResponse.ofSuccess(RESP_ORDER_CREATED, orderMessage);

            default:
                return ApiResponse.of(Status.UNKNOWN_ERROR.getCode(), SOMETHING_WENT_WRONG, null);
        }
    }

    public void flashSaleFailed(String strGoodsId) {
        redService.increment(Prefix.RED_GOODS_KEY_PREFIX + strGoodsId);

        markFalseForGoods(strGoodsId);
    }

    public void markFalseForGoods(String strGoodsId) {
        if (goodsCache.get(strGoodsId) != null) {
            goodsCache.remove(strGoodsId);
        }

        zkService.setSoldOutFalse(strGoodsId);
    }

    public static ConcurrentHashMap<String, Boolean> getGoodsCache() {
        return goodsCache;
    }
}
