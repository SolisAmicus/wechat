package com.solisamicus.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

@Slf4j
public class CuratorUtilsTest {

    @Test
    public void test() {
        CuratorFramework zookeeperClient = CuratorUtils.getZookeeperClient();
        try {
            System.out.println(new String(zookeeperClient.getData().forPath("/welcome")));
        } catch (Exception e) {
            log.error("Error retrieving node data: {}", e.getMessage(), e);
        }
    }
}
