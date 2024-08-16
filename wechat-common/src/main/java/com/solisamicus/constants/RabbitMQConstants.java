package com.solisamicus.constants;

public class RabbitMQConstants {
    public static final String EXCHANGE = "test_exchange";

    public static final String QUEUE = "test_queue";

    public static final String ROUTING_KEY = "solisamicus.wechat.#";

    public static final String ROUTING_KEY_TEST_SEND = "solisamicus.wechat.test.send";

    public static final String ROUTING_KEY_WECHAT_MSG_SEND = "solisamicus.wechat.msg.send";

    public static final String HOST = "127.0.0.1";

    public static final Integer PORT = 5682;

    public static final String USERNAME = "solisamicus";

    public static final String PASSWORD = "rabbitmq";

    public static final String VIRTUALHOST = "wechat-dev";

    public static final Integer MAX_CONNECTION = 20;

    public static final String CHARSET = "UTF-8";
}