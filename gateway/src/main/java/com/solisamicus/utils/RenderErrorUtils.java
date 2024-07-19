package com.solisamicus.utils;

import com.google.gson.Gson;
import com.solisamicus.base.BaseInfoProperties;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RefreshScope
public class RenderErrorUtils extends BaseInfoProperties {
    public static Mono<Void> display(ServerWebExchange exchange, ResponseStatusEnum statusEnum) {
        ServerHttpResponse response = exchange.getResponse();
        GraceJSONResult jsonResult = GraceJSONResult.exception(statusEnum);
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type",MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer buffer = response.bufferFactory().wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
