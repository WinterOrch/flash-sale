package com.winter.flashsale.service;

import com.winter.flashsale.exphandler.Status;
import com.winter.flashsale.exphandler.exception.RedisException;
import com.winter.flashsale.utils.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class RedisService {

    StringRedisTemplate strRedisTemplate;
    RedisTemplate<String, Long> longRedisTemplate;

    public RedisService(StringRedisTemplate strRedisTemplate, RedisTemplate<String, Long> longRedisTemplate) {
        this.strRedisTemplate = strRedisTemplate;
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

    public <T> T get(String key, Class<T> clazz) {
        String result = strRedisTemplate.opsForValue().get(key);

        if (result == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return StringUtils.string2Bean(result, clazz);
        }
    }
}
