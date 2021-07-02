package com.winter.flashsale.redis.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RateLimiterAspect {

    private static final String RED_LIMIT_PREFIX = "limit:";

    private final StringRedisTemplate strRedisTemplate;
    private final RedisScript<Long> rateLimitRedisScript;

    public RateLimiterAspect(StringRedisTemplate strRedisTemplate, RedisScript<Long> rateLimitRedisScript) {
        this.strRedisTemplate = strRedisTemplate;
        this.rateLimitRedisScript = rateLimitRedisScript;
    }

    /**
     * LUA Rate Limit
     *
     * @param uniKey    uni name for rate-limit target, for example, {func_key}:{user_id}
     * @param max       visiting rate above max shall be limited
     * @param timeout   limit time for setting expiration
     * @param timeUnit  Time Unit
     *
     * @return          false if limit is reached
     */
    private boolean shouldBeLimited(String uniKey, long max, long timeout, TimeUnit timeUnit) {
        String key = RED_LIMIT_PREFIX + uniKey;

        // Generic to Millis
        long ttl = timeUnit.toMillis(timeout);
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;

        Long executeTimes = this.strRedisTemplate.execute(rateLimitRedisScript, Collections.singletonList(key),
                now + "", ttl + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("[{}] has reached visit limit in {} millis, with current limit {}", key, ttl, max);
                return true;
            } else {
                log.info("[{}] has reached visit {} times in {} millis", key, executeTimes, ttl);
                return false;
            }
        }
        return false;
    }
}
