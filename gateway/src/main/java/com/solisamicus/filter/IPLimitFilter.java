package com.solisamicus.filter;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import com.solisamicus.base.BaseInfoProperties;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.utils.IPUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RefreshScope
@Slf4j
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {
    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("continueCounts: {}", continueCounts);
        log.info("timeInterval: {}", timeInterval);
        log.info("limitTimes: {}", limitTimes);
        return doLimit(exchange, chain);
    }

    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);
        final String ipRedisKey = "gateway-ip:" + ip;
        final String ipRedisLimitKey = "gateway-ip:limit:" + ip;
        long limitLeftTimes = redis.ttl(ipRedisLimitKey);
        if (limitLeftTimes > 0) {
            return renderErrorMessage(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        long requestCounts = redis.increment(ipRedisKey, 1);
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }
        if (requestCounts > continueCounts) {
            redis.set(ipRedisLimitKey, ipRedisLimitKey, limitTimes);
            return renderErrorMessage(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        return chain.filter(exchange);
    }

    public Mono<Void> renderErrorMessage(ServerWebExchange exchange, ResponseStatusEnum responseStatusEnum) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        GraceJSONResult graceJSONResult = GraceJSONResult.exception(responseStatusEnum);
        if (!serverHttpResponse.getHeaders().containsKey("Content-Type")) {
            serverHttpResponse.getHeaders().add("Conten-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        serverHttpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        String resultJson = new Gson().toJson(graceJSONResult);
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return serverHttpResponse.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
