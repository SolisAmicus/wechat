package com.solisamicus.netty.rabbitmq;

import com.rabbitmq.client.*;

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
