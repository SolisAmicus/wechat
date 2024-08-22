package com.solisamicus.controller;

import com.solisamicus.exceptions.GraceException;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.netty.ServerNode;
import com.solisamicus.result.PagedGridResult;
import com.solisamicus.service.IChatMessageService;
import com.solisamicus.utils.JsonUtils;
import com.solisamicus.utils.RedisOperator;
import jakarta.annotation.Resource;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.solisamicus.constants.Properties.CHAT_MSG_LIST;
import static com.solisamicus.constants.ZookeeperConstants.NODE_SUFFIX;
import static com.solisamicus.constants.ZookeeperConstants.ZOOKEEPER_SERVER;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private RedisOperator redis;

    @Autowired
    private IChatMessageService chatMessageService;

    @PostMapping("getMyUnReadCounts")
    public GraceJSONResult getMyUnReadCounts(String myId) {
        Map<Object, Object> map = redis.hgetall(CHAT_MSG_LIST + ":" + myId);
        return GraceJSONResult.ok(map);
    }

    @PostMapping("clearMyUnReadCounts")
    public GraceJSONResult clearMyUnReadCounts(String myId, String oppositeId) {
        redis.setHashValue(CHAT_MSG_LIST + ":" + myId, oppositeId, "0");
        return GraceJSONResult.ok();
    }

    @PostMapping("list/{senderId}/{receiverId}")
    public GraceJSONResult list(@PathVariable("senderId") String senderId,
                                @PathVariable("receiverId") String receiverId,
                                @RequestParam(defaultValue = "1", name = "page") Integer page,
                                @RequestParam(defaultValue = "20", name = "pageSize") Integer pageSize) {
        return GraceJSONResult.ok(chatMessageService.queryChatMsgList(senderId, receiverId, page, pageSize));
    }

    @PostMapping("signRead/{msgId}")
    public GraceJSONResult signRead(@PathVariable("msgId") String msgId) {
        chatMessageService.updateMsgSignRead(msgId);
        return GraceJSONResult.ok();
    }

    @Resource(name = "curatorClient")
    private CuratorFramework zookeeperClient;

    @PostMapping("getNettyOnlineInfo")
    public GraceJSONResult getNettyOnlineInfo() {
        List<String> zookeeperServers = null;
        try {
            zookeeperServers = zookeeperClient.getChildren().forPath(ZOOKEEPER_SERVER);
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }
        List<ServerNode> serverNodes = new ArrayList<>();
        for (String zookeeperServer : zookeeperServers) {
            try {
                String node = new String(zookeeperClient.getData().forPath(String.format("%s/%s", ZOOKEEPER_SERVER, zookeeperServer)));
                ServerNode serverNode = JsonUtils.jsonToPojo(node, ServerNode.class);
                serverNodes.add(serverNode);
            } catch (Exception e) {
                GraceException.display(ResponseStatusEnum.FAILED);
            }
        }
        ServerNode minServerNode = serverNodes
                .stream()
                .min(Comparator.comparing(ServerNode::getOnlineCounts))
                .get();
        return GraceJSONResult.ok(minServerNode);
    }
}
