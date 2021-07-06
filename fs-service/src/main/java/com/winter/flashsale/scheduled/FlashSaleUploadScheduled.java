package com.winter.flashsale.scheduled;

import com.winter.flashsale.consts.Prefix;
import com.winter.flashsale.service.FlashSaleService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleUploadScheduled {

    @Resource
    private FlashSaleService flashSaleService;

    @Resource
    private RedissonClient redissonClient;

    public void uploadFlashSaleInfoLatestThreeDays() {
        // Redisson Distributed Lock
        RLock lock = redissonClient.getLock(Prefix.REDISSON_UPLOAD_STOCK);
        lock.lock(5L, TimeUnit.SECONDS);

        try {

        } finally {
            lock.unlock();
        }
    }
}
