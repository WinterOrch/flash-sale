package com.winter.flashsale.zookeeper;

import com.winter.common.model.Status;
import com.winter.common.exception.ZookeeperException;
import com.winter.flashsale.consts.Prefix;
import com.winter.flashsale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoodsWatcher implements Watcher, ApplicationContextAware {

    private ZooKeeper zooKeeper;

    private ApplicationContext applicationContext;

    public GoodsWatcher() {
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

        System.out.println("************************zookeeper***start*****************");

        if (watchedEvent.getType() == Event.EventType.None && watchedEvent.getPath() == null) {
            log.info("============[Zookeeper Connected]============");
            try {
                if (zooKeeper == null) {
                    zooKeeper = applicationContext.getBean(ZooKeeper.class);
                }

                // 创建 zookeeper 商品售完信息根节点
                if (zooKeeper.exists(Prefix.ZK_GOODS_ROOT_PATH, false) == null) {
                    zooKeeper.create(Prefix.ZK_GOODS_ROOT_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException | InterruptedException e) {
                throw new ZookeeperException(Status.UNKNOWN_ERROR.getCode(), "Create Zookeeper Goods Root Path Error ... " + e);
            }

        } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            try {
                // 获取节点路径
                String path = watchedEvent.getPath();
                // 获取节点数据
                String soldOut = new String(zooKeeper.getData(path, true, new Stat()));
                // 获取商品 Id
                String productId = path.substring(path.lastIndexOf("/") + 1);
                // 处理当前服务器对应 JVM 缓存
                if("false".equals(soldOut)){
                    log.info("ZNode: " + path + " was marked False");

                    // remove does nothing when specified key isn't contained in map,
                    // so no need to check if it contains the key
                    FlashSaleService.getGoodsCache().remove(productId);
                }
            } catch (KeeperException | InterruptedException e) {
                throw new ZookeeperException(Status.UNKNOWN_ERROR.getCode(),
                        "Zookeeper data reading Error when processing NodeDataChanged event ... " + e);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
