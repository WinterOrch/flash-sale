package com.winter.flashsale.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zookeeper")
public class ZkProperties {

    private int timeout;
    private String address;
    private String serverPort;

    public int getTimeout() {
        return timeout;
    }

    public String getAddress() {
        return address;
    }

    public String getServerPort() {
        return this.serverPort;
    }
}
