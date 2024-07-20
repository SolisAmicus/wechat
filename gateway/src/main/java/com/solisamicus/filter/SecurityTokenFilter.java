package com.solisamicus.filter;

import com.solisamicus.config.ExcludeUrlProperties;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.utils.RedisOperator;
import com.solisamicus.utils.RenderErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.solisamicus.constants.Properties.*;

@Component
@Slf4j
public class SecurityTokenFilter implements GlobalFilter, Ordered {
    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;

    @Autowired
    private RedisOperator redis;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        log.info("SecurityFilterToken url = {}", url);
        List<String> excludeList = excludeUrlProperties.getUrls();
        if (!CollectionUtils.isEmpty(excludeList)) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    log.info("URL [{}] is in the exclude list, allowing request to proceed.", url);
                    return chain.filter(exchange);
                }
            }
        }
        String fileStart = excludeUrlProperties.getFileStart();
        if (StringUtils.isNotBlank(fileStart)) {
            boolean matchFileStart = antPathMatcher.matchStart(fileStart, url);
            if (matchFileStart) {
                log.info("URL [{}] matches file start pattern, allowing request to proceed.", url);
                return chain.filter(exchange);
            }
        }

        log.info("The current request path [{}] is intercepted...", url);
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userId = headers.getFirst(HEADER_USER_ID);
        String userToken = headers.getFirst(HEADER_USER_TOKEN);
        log.info("userId = {}", userId);
        log.info("userToken = {}", userToken);

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String redisToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            if (redisToken != null && redisToken.equals(userToken)) {
                log.info("Token validation successful for userId [{}], allowing request to proceed.", userId);
                return chain.filter(exchange);
            } else {
                log.warn("Token validation failed for userId [{}].", userId);
            }
        } else {
            log.warn("Missing or empty userId/userToken in request headers.");
        }
        return RenderErrorUtils.display(exchange, ResponseStatusEnum.UN_LOGIN);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
