package com.solisamicus.filter;

import com.solisamicus.utils.RedisOperator;
import com.solisamicus.utils.RenderErrorUtils;
import lombok.extern.slf4j.Slf4j;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
@Slf4j
public class IPLimitFilter implements GlobalFilter, Ordered {
    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    @Autowired
    private RedisOperator redis;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("continueCounts: {},timeInterval: {},limitTimes: {}", continueCounts, timeInterval, limitTimes);
        return doLimit(exchange, chain);
    }

    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtils.getIP(request);
        final String ipRedisKey = "gateway-ip:" + ip;
        final String ipRedisLimitKey = "gateway-ip:limit:" + ip;
        long limitLeftTimes = redis.ttl(ipRedisLimitKey);
        if (limitLeftTimes > 0) {
            return RenderErrorUtils.display(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        long requestCounts = redis.increment(ipRedisKey, 1);
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }
        if (requestCounts > continueCounts) {
            redis.set(ipRedisLimitKey, ipRedisLimitKey, limitTimes);
            return RenderErrorUtils.display(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
