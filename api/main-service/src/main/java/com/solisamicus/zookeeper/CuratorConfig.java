package com.solisamicus.zookeeper;

import com.solisamicus.pojo.netty.ServerNode;
import com.solisamicus.utils.JsonUtils;
import com.solisamicus.utils.RedisOperator;
import lombok.Data;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.solisamicus.constants.JedisConstants.NETTY_PORT;
import static com.solisamicus.constants.ZookeeperConstants.ZOOKEEPER_SERVER;

@Component
@Data
@ConfigurationProperties(prefix = "zookeeper.curator")
public class CuratorConfig {
    private String host;
    private Integer port;
    private Integer connectionTimeout;
    private Integer sessionTimeout;
    private Integer sleepBetweenRetry;
    private Integer maxRetries;
    private String namespace;

    @Bean("curatorClient")
    public CuratorFramework curatorClient() {
        RetryPolicy backoffRetry = new ExponentialBackoffRetry(sleepBetweenRetry, maxRetries);
        CuratorFramework curatorClient = CuratorFrameworkFactory.builder().connectString(host).connectionTimeoutMs(connectionTimeout).sessionTimeoutMs(sessionTimeout).retryPolicy(backoffRetry).namespace(namespace).build();
        curatorClient.start();
        addWatch(ZOOKEEPER_SERVER, curatorClient);
        return curatorClient;
    }

    @Autowired
    private RedisOperator redis;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    public void addWatch(String path, CuratorFramework client) {
        CuratorCache curatorCache = CuratorCache.build(client, path);
        curatorCache.listenable().addListener((type, oldData, data) -> {
            switch (type.name()) {
                case "NODE_CREATED":
                    break;
                case "NODE_CHANGED":
                    break;
                case "NODE_DELETED":
                    ServerNode ServerNode = JsonUtils.jsonToPojo(new String(oldData.getData()), ServerNode.class);
                    String port = String.valueOf(ServerNode.getPort());
                    String queue = "netty_queue_" + port;
                    redis.hdel(NETTY_PORT, port);
                    rabbitAdmin.deleteQueue(queue);
                    break;
                default:
                    break;
            }
        });
        curatorCache.start();
    }
}

