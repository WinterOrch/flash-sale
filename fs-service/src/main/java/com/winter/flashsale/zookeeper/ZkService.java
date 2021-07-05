package com.winter.flashsale.zookeeper;

import com.winter.flashsale.consts.Prefix;
import com.winter.common.model.Status;
import com.winter.common.exception.ZookeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Service;

@Service
public class ZkService {

    private final ZooKeeper zooKeeper;

    public ZkService(ZooKeeper zooKeeperClient) {
        this.zooKeeper = zooKeeperClient;
    }

    public void setSoldOutFalse(String strGoodsId) {
        String zkGoodsPath = Prefix.ZK_GOODS_ROOT_PATH + "/" + strGoodsId;
        try {
            if (zooKeeper.exists(zkGoodsPath, true) != null) {
                zooKeeper.setData(zkGoodsPath, "false".getBytes(), -1);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new ZookeeperException(Status.UNKNOWN_ERROR.getCode(), "Zookeeper failed ... " + e);
        }
    }

    public void setSoldOutTrue(String strGoodsId) {
        String zkGoodsPath = Prefix.ZK_GOODS_ROOT_PATH + "/" + strGoodsId;
        try {
            if (zooKeeper.exists(zkGoodsPath, true) == null) {
                zooKeeper.create(zkGoodsPath, "true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zooKeeper.exists(zkGoodsPath, true);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new ZookeeperException(Status.UNKNOWN_ERROR.getCode(), "Zookeeper failed ... " + e);
        }
    }
}
