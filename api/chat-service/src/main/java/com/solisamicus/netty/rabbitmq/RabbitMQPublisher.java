package com.solisamicus.netty.rabbitmq;

import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.pojo.netty.DataContent;
import com.solisamicus.utils.JsonUtils;

import static com.solisamicus.constants.RabbitMQConstants.*;

public class RabbitMQPublisher {
    public static void sendMsgToSave(ChatMsg chatMsg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        connectUtils.sendMsg(JsonUtils.objectToJson(chatMsg), EXCHANGE, ROUTING_KEY_WECHAT_MSG_SEND);
    }

    public static void sendMsgToOtherNettyServers(DataContent data) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        connectUtils.sendMsg(JsonUtils.objectToJson(data),FANOUT_EXCHANGE,"");
    }
}
