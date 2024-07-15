package com.solisamicus.base;

import com.solisamicus.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseInfoProperties {

    @Autowired
    public RedisOperator redis;

    public static final String SYMBOL_DOT = ".";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final String TOKEN_USER_PREFIX = "app";

    public static final String MOBILE_SMSCODE = "mobile:smscode";
}
