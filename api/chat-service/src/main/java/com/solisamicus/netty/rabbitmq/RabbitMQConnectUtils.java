package com.solisamicus.netty.rabbitmq;

import com.rabbitmq.client.*;
import com.solisamicus.netty.websocket.UserSession;
import com.solisamicus.pojo.netty.DataContent;
import com.solisamicus.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.solisamicus.constants.RabbitMQConstants.*;

public class RabbitMQConnectUtils {

    private final List<Connection> connections = new ArrayList<>();

    public ConnectionFactory factory;

    private void initFactory() {
        try {
            if (factory == null) {
                factory = new ConnectionFactory();
                factory.setHost(HOST);
                factory.setPort(PORT);
                factory.setUsername(USERNAME);
                factory.setPassword(PASSWORD);
                factory.setVirtualHost(VIRTUALHOST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message, String queue) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(CHARSET));
        channel.close();
        setConnection(connection);
    }

    public void sendMsg(String message, String exchange, String routingKey) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(CHARSET));
        channel.close();
        setConnection(connection);
    }

    public GetResponse basicGet(String queue, boolean autoAck) throws Exception {
        GetResponse getResponse = null;
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        getResponse = channel.basicGet(queue, autoAck);
        channel.close();
        setConnection(connection);
        return getResponse;
    }

    public Connection getConnection() throws Exception {
        return getAndSetConnection(true, null);
    }

    public void setConnection(Connection connection) throws Exception {
        getAndSetConnection(false, connection);
    }

    public void listen(String queue, String exchange) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true, false, false, null);
        channel.queueDeclare(queue, true, false, false, null);
        channel.queueBind(queue, exchange, "");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String data = new String(body);
                String exchange = envelope.getExchange();
                if (exchange.equalsIgnoreCase(FANOUT_EXCHANGE)) {
                    DataContent dataContent = JsonUtils.jsonToPojo(data, DataContent.class);
                    // 广播至 netty server 集群节点并发送给其他用户消息
                    String senderId = dataContent.getChatMsg().getSenderId();
                    String receiverId = dataContent.getChatMsg().getReceiverId();
                    List<io.netty.channel.Channel> receiverChannels = UserSession.getMultiChannels(receiverId);
                    UserSession.sendToTarget(receiverChannels, dataContent);
                    // 广播至 netty server 集群节点并同步给自身设备消息
                    String currentChannelId = dataContent.getExtend();
                    List<io.netty.channel.Channel> senderChannels = UserSession.getMyOtherMultiChannels(senderId, currentChannelId);
                    UserSession.sendToTarget(senderChannels, dataContent);
                }
            }
        };
        channel.basicConsume(queue, true, consumer);
    }

    private synchronized Connection getAndSetConnection(boolean isGet, Connection connection) throws Exception {
        initFactory();

        if (isGet) {
            if (connections.isEmpty()) {
                return factory.newConnection();
            }
            Connection newConnection = connections.get(0);
            connections.remove(0);
            if (newConnection.isOpen()) {
                return newConnection;
            } else {
                return factory.newConnection();
            }
        } else {
            if (connections.size() < MAX_CONNECTION) {
                connections.add(connection);
            }
            return null;
        }
    }
}
