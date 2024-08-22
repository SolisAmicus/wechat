package com.solisamicus.netty.zookeeper;

import com.solisamicus.pojo.netty.ServerNode;
import com.solisamicus.utils.CuratorUtils;
import com.solisamicus.utils.JsonUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import static com.solisamicus.constants.ZookeeperConstants.NODE_SUFFIX;
import static com.solisamicus.constants.ZookeeperConstants.ZOOKEEPER_SERVER;

public class ZookeeperRegister {
    public static void register(String host, int port) throws Exception {
        CuratorFramework zookeeperClient = CuratorUtils.getZookeeperClient();
        Stat stat = zookeeperClient.checkExists().forPath(ZOOKEEPER_SERVER);
        // servers
        if (stat == null) {
            zookeeperClient
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(ZOOKEEPER_SERVER);
        }
        ServerNode serverNode = new ServerNode(host, port, 0);
        // servers/dev-
        zookeeperClient
                .create()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(String.format("%s%s", ZOOKEEPER_SERVER , NODE_SUFFIX), JsonUtils.objectToJson(serverNode).getBytes());
    }
}
