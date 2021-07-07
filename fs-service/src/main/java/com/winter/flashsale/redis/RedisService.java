package com.winter.flashsale.redis;

import com.winter.flashsale.consts.Prefix;
import com.winter.common.model.Status;
import com.winter.common.exception.RedisException;
import com.winter.common.utils.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    StringRedisTemplate strRedisTemplate;

    RedisScript<Long> flashSaleIfExistScript;

    BoundHashOperations<String, String, String> goodsInfoCache;

    public enum RedResponse {
        NOT_IN_STOCK(-1),
        ORDER_CREATED(1),
        ORDER_EXISTS(2),
        NOT_ENOUGH(3),
        STOCK_OUT(0);

        RedResponse(int Code) {
            statusCode = Code;
        }

        int statusCode;
    }

    public RedisService(StringRedisTemplate strRedisTemplate, RedisScript<Long> flashSaleIfExistScript) {
        this.strRedisTemplate = strRedisTemplate;
        this.flashSaleIfExistScript = flashSaleIfExistScript;

        this.goodsInfoCache = strRedisTemplate.boundHashOps(Prefix.RED_INFO_GOODS_KEY);
    }

    @PostConstruct
    public void loadScript() {
        strRedisTemplate.execute((RedisCallback<String>) connection ->
                connection.scriptLoad(flashSaleIfExistScript.getScriptAsString().getBytes()));
    }

    public boolean doesOrderAlreadyExist(long userId, long goodsId) {
        Boolean res = strRedisTemplate.hasKey(userId + "_" + goodsId);

        if (res == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return res;
        }
    }

    public RedResponse redFlashSale(String goodsId, String userId, Integer num) {
        List<String> keys = new ArrayList<>();
        keys.add(Prefix.RED_GOODS_KEY_PREFIX + goodsId);
        keys.add(Prefix.RED_ORDER_KEY_PREFIX + userId + "_" + goodsId);

        Long res = strRedisTemplate.execute(flashSaleIfExistScript, keys, num);
        if (res == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else if (res == 1L) {
            return RedResponse.ORDER_CREATED;
        } else if (res == 2L) {
            return RedResponse.ORDER_EXISTS;
        } else if (res == 0L) {
            return RedResponse.STOCK_OUT;
        } else if (res == 3L) {
            return RedResponse.NOT_ENOUGH;
        } else {
            return RedResponse.NOT_IN_STOCK;
        }
    }

    public void increment(String key) {
        strRedisTemplate.opsForValue().increment(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String result = strRedisTemplate.opsForValue().get(key);

        if (result == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return StringUtils.string2Bean(result, clazz);
        }
    }

    public String get(String key) {
        String result = strRedisTemplate.opsForValue().get(key);

        if (result == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return result;
        }
    }

    public void leftPushAll(String key, Collection<String> collection) {
        strRedisTemplate.opsForList().leftPushAll(key, collection);
    }

    public List<String> getRange(String key, long l1, long l2) {
        return strRedisTemplate.opsForList().range(key, l1, l2);
    }

    public boolean hasKey(String key) {
        Boolean res = strRedisTemplate.hasKey(key);

        if (res == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return res;
        }
    }

    public boolean setIfAbsent(String key, String value, long duration, TimeUnit timeUnit) {
        Boolean res = strRedisTemplate.opsForValue().setIfAbsent(key, value, duration, timeUnit);

        if (res == null) {
            throw new RedisException(Status.UNKNOWN_ERROR.getCode(), "Null Result");
        } else {
            return res;
        }
    }

    public void set(String key, String value, long duration, TimeUnit timeUnit) {
        strRedisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    public void set(String key, String value) {
        strRedisTemplate.opsForValue().set(key, value);
    }

    public void batchSetOrExpire(Map<String, String> map, Long milliseconds) {
        RedisSerializer<String> serializer = strRedisTemplate.getStringSerializer();
        strRedisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((key, value) -> {
                connection.set(serializer.serialize(key), serializer.serialize(value), Expiration.milliseconds(milliseconds), RedisStringCommands.SetOption.UPSERT);
            });
            return null;
        }, serializer);
    }

    public void multiDelete(List<String> keys) {
        strRedisTemplate.delete(keys);
    }

    public void delete(String key) {
        strRedisTemplate.delete(key);
    }

    public BoundHashOperations<String, String, String> boundHashOps(String key) {
        return strRedisTemplate.boundHashOps(key);
    }

    public String getGoodsInfo (String id) {
        return goodsInfoCache.get(id);
    }
}
