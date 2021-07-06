package com.winter.flashsale.service;

import com.alibaba.fastjson.JSONObject;
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
import com.winter.flashsale.vo.FlashSaleSessionVO;
import com.winter.flashsale.zookeeper.ZkService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FlashSaleService {

    /**
     * JVM Cache for flash sale goods
    * */
    private static final ConcurrentHashMap<String, Boolean> goodsCache = new ConcurrentHashMap<>();

    private CouponFeign couponFeign;

    private final ZkService zkService;
    private final RedisService redService;
    private final RedissonClient redissonClient;
    private final Sender sender;

    private final SnowFlake snowFlake;

    public FlashSaleService(ZkService zkService, RedisService redisService, Sender sender, RedissonClient redissonClient,
                            @Value("${snowflake.dcid}") long dataCentreId, @Value("${snowflake.machineid}") long machineId) {
        this.couponFeign = new CouponFeignImpl();

        this.zkService = zkService;
        this.redService = redisService;
        this.redissonClient = redissonClient;
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

    public void uploadFlashSaleSession() {
        ApiResponse session = couponFeign.getLatestFlashSaleSession();
        if (session.isSuccessful()) {
            //  Query Flash Sale Session
            List<FlashSaleSessionVO> data = session.getData(new TypeReference<List<FlashSaleSessionVO>>() {});

            if (!CollectionUtils.isEmpty(data)) {

            }

        }
    }

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

    // TODO 其实这里是问题的，hasKey 和 leftPushAll 最好能够放在一个 LUA 脚本中通过原子操作完成
    private void saveSessionInfo(List<FlashSaleSessionVO> sessionData) {
        sessionData.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = Prefix.RED_SESSION_KEY_PREFIX + startTime + "_" + endTime;

            /*
            *   这里对原读写方案进行了优化，原方案通过hasKey判断是否写入过Session信息，有并发不安全因素，setnx更好
            *   而且原文通过lpushall存入List<String>其实并不会将全部String插进list，而是将整个List作为一个String存进去
            * */
            boolean notHaveKey = redService.setIfAbsent(key, "1", 3L, TimeUnit.SECONDS);
            if (notHaveKey) {
                List<String> collect = session.getRelatedGoods().stream()
                        .map(item -> item.getPromotionSessionId() + "_" + item.getGoodsId().toString()).collect(Collectors.toList());
                redService.set(key, JSONObject.toJSONString(collect), 5L, TimeUnit.DAYS);
            }
        });
    }
}
