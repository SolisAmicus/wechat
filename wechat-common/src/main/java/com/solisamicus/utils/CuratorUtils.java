package com.solisamicus.utils;

import lombok.Getter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.solisamicus.constants.CuratorConstants.*;

public class CuratorUtils {
    @Getter
    private static final CuratorFramework zookeeperClient;

    static {
        zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(String.format("%s:%d", HOST, PORT))
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_BETWEEN_RETRY, MAX_RETRIES))
                .namespace(NAMESPACE)
                .build();
        zookeeperClient.start();
    }
}
