package com.winter.flashsale.scheduled;

import com.winter.flashsale.consts.Prefix;
import com.winter.flashsale.service.FlashSaleService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleUploadScheduled {

    @Resource
    private FlashSaleService flashSaleServiceImpl;

    @Resource
    private RedissonClient redissonClient;

//    @Scheduled(cron = "*/8 * * * * ?")
    public void uploadFlashSaleInfoLatestThreeDays() {
        // Redisson Distributed Lock
        RLock lock = redissonClient.getLock(Prefix.REDISSON_UPLOAD_STOCK);
        lock.lock(10L, TimeUnit.SECONDS);

        try {
            flashSaleServiceImpl.uploadFlashSaleSession();
        } finally {
            lock.unlock();
        }
    }
}
