package com.solisamicus.constants;

public class JedisConstants {
    public static final String HOST = "127.0.0.1";
    public static final Integer PORT = 16379;
    public static final String PASSWORD = "redis";
    public static final Integer MAX_TOTAL = 10;
    public static final Integer MAX_IDLE = 10;
    public static final Integer MIN_IDLE = 5;
    public static final Integer MAX_WAIT_MILLIS = 1500;
    public static final Integer TIMEOUT = 1000;

    // Netty
    public static final String NETTY_PORT = "netty_port";
    public static final Integer DEFAULT_PORT = 875;
    public static final String ONLINE_COUNTS = "0";
    public static final Integer STEP = 10;
}
