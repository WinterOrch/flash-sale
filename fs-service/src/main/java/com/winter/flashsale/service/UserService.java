package com.winter.flashsale.service;

import com.winter.flashsale.mq.MQSender;
import com.winter.flashsale.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private UserMapper userMapper;

    private RedisService redisService;

    private MQSender mqSender;

    public boolean
}
