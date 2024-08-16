package com.solisamicus.netty.rabbitmq;

import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.utils.JsonUtils;

import static com.solisamicus.constants.RabbitMQConstants.EXCHANGE;
import static com.solisamicus.constants.RabbitMQConstants.ROUTING_KEY_WECHAT_MSG_SEND;

public class RabbitMQPublisher {
    public static void sendMsgToSave(ChatMsg msg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        connectUtils.sendMsg(JsonUtils.objectToJson(msg), EXCHANGE, ROUTING_KEY_WECHAT_MSG_SEND);
    }

    public static void sendMsgToOtherNettyServer(String msg) throws Exception {
        // todo
    }
}
