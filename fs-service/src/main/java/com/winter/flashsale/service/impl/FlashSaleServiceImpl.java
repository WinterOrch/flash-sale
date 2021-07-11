package com.winter.flashsale.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winter.common.message.FlashSaleOrderMessage;
import com.winter.common.model.ApiResponse;
import com.winter.common.model.Status;
import com.winter.common.utils.uuid.SnowFlake;
import com.winter.flashsale.consts.Prefix;
import com.winter.flashsale.feign.CouponFeign;
import com.winter.flashsale.feign.impl.CouponFeignImpl;
import com.winter.flashsale.mq.Sender;
import com.winter.flashsale.redis.RedisService;
import com.winter.flashsale.service.FlashSaleService;
import com.winter.flashsale.to.FlashSaleGoodsRedTO;
import com.winter.flashsale.vo.FlashSaleGoodsVO;
import com.winter.flashsale.vo.FlashSaleSessionVO;
import com.winter.flashsale.zookeeper.ZkService;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    /**
     * JVM Cache for flash sale goods
    * */
    private static final ConcurrentHashMap<String, Boolean> goodsCache = new ConcurrentHashMap<>();

    private final CouponFeign couponFeign;

    private final ZkService zkService;
    private final RedisService redService;

    private final Sender sender;

    private final SnowFlake snowFlake;

    public FlashSaleServiceImpl(ZkService zkService, RedisService redisService,
                                Sender sender, SnowFlake snowFlake) {
        this.couponFeign = new CouponFeignImpl();

        this.zkService = zkService;
        this.redService = redisService;
        this.sender = sender;

        this.snowFlake = snowFlake;
    }

    /**
     * Response Messages for Flash Sale
     * */
    public static final String RESP_SOLD_OUT = "goods has been sold out";
    public static final String STOCK_NOT_ENOUGH = "stock not enough";
    public static final String RESP_ORDER_CREATED = "order has been created";
    public static final String RESP_ORDER_EXISTS = "order already exists";
    public static final String LIMIT_EXCEEDED = "number exceeded";
    public static final String STOCK_NOT_EXISTS = "what the hell";
    public static final String SESSION_ENDS = "flash sale ends";
    public static final String SOMETHING_WENT_WRONG = "please try again";

    public void uploadFlashSaleSession() {
        ApiResponse session = couponFeign.getLatestFlashSaleSession();
        if (session.isSuccessful()) {
            //  Query Flash Sale Session
            List<FlashSaleSessionVO> data = session.getData(new TypeReference<List<FlashSaleSessionVO>>() {});

            if (!CollectionUtils.isEmpty(data)) {
                this.saveSessionInfo(data);
            }
        }
    }

    /**
     * 秒杀过程的主要逻辑
     * @param goodsId   {goodsId}
     * created in 17:39 2021/7/5
     */
    // TODO 目前每个用户抢购的订单占位符是没有过期时间的，最好给他设上
    public ApiResponse doFlashSale(String sessionId, String goodsId, String random, String userId, Integer num) {

        String key = sessionId + "_" + goodsId;

        if (goodsCache.containsKey(key)) {

            return ApiResponse.ofMessage(RESP_SOLD_OUT);
        }

        String json = redService.getGoodsInfo(key);
        if (StringUtils.isEmpty(json)) {

            return ApiResponse.ofMessage(STOCK_NOT_EXISTS);
        } else {
            // Check Random and GoodsId
            FlashSaleGoodsRedTO redTO = JSON.parseObject(json, FlashSaleGoodsRedTO.class);

            long time = new Date().getTime();
            if (time >= redTO.getStartTime() && time <= redTO.getEndTime()) {
                if (random.equals(redTO.getRandomCode()) && key.equals(redTO.getPromotionSessionId() + "_" + redTO.getGoodsId())) {

                    // Check Purchase Limit
                    if (num <= redTO.getPurchaseLimit() && num > 0) {

                        RedisService.RedResponse result = redService.redFlashSale(key, userId, num);

                        switch (result) {
                            case STOCK_OUT:
                                goodsCache.put(key, true);
                                zkService.setSoldOutTrue(key);
                                return ApiResponse.ofMessage(RESP_SOLD_OUT);

                            case NOT_IN_STOCK:
                                return ApiResponse.ofMessage(STOCK_NOT_EXISTS);

                            case ORDER_EXISTS:
                                return ApiResponse.ofMessage(RESP_ORDER_EXISTS);

                            case ORDER_CREATED:
                                FlashSaleOrderMessage orderMessage =
                                        new FlashSaleOrderMessage(snowFlake.nextId(), sessionId, userId, goodsId, num);
                                sender.sendDirectFlashSaleOrder(orderMessage);
                                return ApiResponse.ofSuccess(RESP_ORDER_CREATED, orderMessage);

                            case NOT_ENOUGH:
                                return ApiResponse.ofMessage(STOCK_NOT_ENOUGH);

                            default:
                                return ApiResponse.of(Status.UNKNOWN_ERROR.getCode(), SOMETHING_WENT_WRONG, null);
                        }

                    } else {

                        return ApiResponse.ofMessage(LIMIT_EXCEEDED);
                    }
                }
            } else {

                return ApiResponse.ofMessage(SESSION_ENDS);
            }
        }

        return ApiResponse.of(Status.UNKNOWN_ERROR.getCode(), SOMETHING_WENT_WRONG, null);
    }

    public void clearUpSession(String sessionKey) {
        /*
         * fs:session:{startTime}_{endTime}
         * */
        // Clear Redis Stock Cache
        List<String> range = redService.getRange(Prefix.RED_SESSION_KEY_PREFIX + sessionKey, -100, 100);
        redService.multiDelete(range);


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

    private void saveSessionInfo(List<FlashSaleSessionVO> sessionData) {
        sessionData.forEach(session -> {
            /*
             * Save Session Info
             *
             * fs:session:{startTime}_{endTime}
             *  - List<String> collect
             * */
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = Prefix.RED_SESSION_KEY_PREFIX + startTime + "_" + endTime;

            boolean hasKey = redService.hasKey(key);
            if (!hasKey) {
                List<String> collect = session.getRelatedGoods().stream()
                        .map(item -> item.getPromotionSessionId() + "_" + item.getGoodsId().toString()).collect(Collectors.toList());
                redService.leftPushAll(key, collect);
            }

            /*
             * Save Goods Info in Hash
             *
             * fs:goods
             *  -  {promotionSessionId}_{goodsId}
             *  -  {promotionSessionId}_{goodsId}
             *  ...
             * */

            BoundHashOperations<String, String, String> boundHash = redService.boundHashOps(Prefix.RED_INFO_GOODS_KEY);

            Map<String, String> redTOMap = new HashMap<>();
            Map<String, String> stockMap = new HashMap<>();
            for (FlashSaleGoodsVO goodsVO : session.getRelatedGoods()) {
                String goodsKey = goodsVO.getPromotionSessionId() + "_" + goodsVO.getGoodsId();

                Boolean hasGoods = boundHash.hasKey(goodsKey);
                if (null != hasGoods && !hasGoods) {
                    FlashSaleGoodsRedTO redTO = new FlashSaleGoodsRedTO();

                    // TODO load basic information for goods using productFeign API
                    /*productFeign.getGoodsInfo();*/

                    redTO.setFlashSaleCount(goodsVO.getFlashSaleCount());
                    redTO.setFlashSalePrice(goodsVO.getFlashSalePrice());
                    redTO.setFlashSaleSort(goodsVO.getFlashSaleSort());
                    redTO.setFlashSaleSort(goodsVO.getFlashSaleSort());
                    redTO.setGoodsId(goodsVO.getGoodsId());
                    redTO.setPromotionSessionId(goodsVO.getPromotionSessionId());
                    redTO.setPurchaseLimit(goodsVO.getFlashSaleLimit());

                    redTO.setStartTime(session.getStartTime().getTime());
                    redTO.setEndTime(session.getEndTime().getTime());

                    redTO.setRandomCode(UUID.randomUUID().toString().replace("-", ""));

                    redTOMap.put(goodsKey, JSON.toJSONString(redTO));
                    /*
                     * IMPORTANT: Load Stock
                     *
                     * {promotionSessionId}_{goodsId}
                     * {stock}
                     * */
                    stockMap.put(goodsKey, goodsVO.getFlashSaleCount().toString());
                }
            }

            boundHash.putAll(redTOMap);
            redService.batchSetOrExpire(stockMap, session.getEndTime().getTime() - new Date().getTime());
        });
    }
}
