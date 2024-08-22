package com.solisamicus.netty.zookeeper;

import com.solisamicus.pojo.netty.ServerNode;
import com.solisamicus.utils.CuratorUtils;
import com.solisamicus.utils.JsonUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.List;
import java.util.Objects;

import static com.solisamicus.constants.ZookeeperConstants.ZOOKEEPER_SERVER;

public class ZookeeperCustom {
    public static void incrementOnlineCounts(ServerNode serverNode) throws Exception {
        dealOnlineCounts(serverNode, 1);
    }

    public static void decrementOnlineCounts(ServerNode serverNode) throws Exception {
        dealOnlineCounts(serverNode, -1);
    }

    public static void dealOnlineCounts(ServerNode serverNode, Integer counts) throws Exception {
        CuratorFramework zookeeperClient = CuratorUtils.getZookeeperClient();
        InterProcessReadWriteLock readWriteLock = new InterProcessReadWriteLock(zookeeperClient, "/rw-locks");
        readWriteLock.writeLock().acquire();
        List<String> zookeeperServers = zookeeperClient.getChildren().forPath(ZOOKEEPER_SERVER);
        for (String zookeeperServer : zookeeperServers) {
            String path = String.format("%s/%s", ZOOKEEPER_SERVER, zookeeperServer);
            String node = new String(zookeeperClient.getData().forPath(path));
            ServerNode pendingNode = JsonUtils.jsonToPojo(node, ServerNode.class);
            assert pendingNode != null;
            if (Objects.equals(pendingNode.getHost(), serverNode.getHost()) && Objects.equals(pendingNode.getPort(), serverNode.getPort())) {
                pendingNode.setOnlineCounts(pendingNode.getOnlineCounts() + counts);
                String nodeJson = JsonUtils.objectToJson(pendingNode);
                zookeeperClient.setData().forPath(path, nodeJson.getBytes());
            }
        }
        readWriteLock.writeLock().release();
    }
}
