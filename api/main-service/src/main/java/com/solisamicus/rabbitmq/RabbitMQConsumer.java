package com.solisamicus.rabbitmq;

import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.service.IChatMessageService;
import com.solisamicus.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.solisamicus.constants.RabbitMQConstants.*;

@Component
@Slf4j
public class RabbitMQConsumer {
    @Autowired
    private IChatMessageService chatMessageService;

    @RabbitListener(queues = {QUEUE})
    public void watchQueue(String payload, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equals(ROUTING_KEY_WECHAT_MSG_SEND)) {
            ChatMsg chatMsg = JsonUtils.jsonToPojo(payload, ChatMsg.class);
            chatMessageService.saveMsg(chatMsg);
        }
    }
}
