package com.solisamicus.constants;

public class CuratorConstants {
    public static final String HOST = "127.0.0.1";
    public static final Integer PORT = 2181;
    public static final Integer CONNECTION_TIMEOUT = 30 * 1000;
    public static final Integer SESSION_TIMEOUT = 3 * 1000;
    public static final Integer SLEEP_BETWEEN_RETRY = 2 * 1000;
    public static final Integer MAX_RETRIES = 3;
    public static final String NAMESPACE = "wechat-dev";
}
