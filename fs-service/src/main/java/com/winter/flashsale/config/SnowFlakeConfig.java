package com.winter.flashsale.config;

import com.winter.common.utils.uuid.SnowFlake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowFlakeConfig {

    @Bean
    public SnowFlake snowFlake(@Value("${snowflake.dcid}") long dcId, @Value("${snowflake.machineid}") long machineId) {
        return new SnowFlake(dcId, machineId);
    }
}
