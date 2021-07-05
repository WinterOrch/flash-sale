package com.winter.flashsale.zookeeper;

import com.winter.common.model.Status;
import com.winter.common.exception.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class ZkConfig {
    ZkProperties properties;

    public ZkConfig(ZkProperties zkProperties) {
        this.properties = zkProperties;
    }

    @Bean
    public ZooKeeper zooKeeperClient() {
        ZooKeeper zooKeeper;

        try {
            GoodsWatcher watcher = new GoodsWatcher();
            // 连接成功后异步回调Watcher监听
            zooKeeper = new ZooKeeper(properties.getAddress(), properties.getTimeout(), watcher);

            log.info("Init Zookeeper Connection State ... = {}", zooKeeper.getState());

        } catch (IOException e) {
            throw new ZookeeperException(Status.UNKNOWN_ERROR.getCode(), "Init Zookeeper Connection Error ... " + e);

        }

        return zooKeeper;
    }
}
