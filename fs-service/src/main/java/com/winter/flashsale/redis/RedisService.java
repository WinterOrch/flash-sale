package com.winter.flashsale.redis;

import com.winter.flashsale.exphandler.Status;
import com.winter.flashsale.exphandler.exception.RedisException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisService {

    RedisTemplate<String, Long> longRedisTemplate;

    public RedisService(RedisTemplate<String, Long> longRedisTemplate) {
        this.longRedisTemplate = longRedisTemplate;
    }

    public boolean doesOrderAlreadyExist(long userId, long goodsId) {
        Boolean res = longRedisTemplate.hasKey(userId + "_" + goodsId);

        if (res == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return res;
        }
    }
}
