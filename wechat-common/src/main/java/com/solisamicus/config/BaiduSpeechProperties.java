package com.solisamicus.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@PropertySource(value = "classpath:baiduSpeech.properties", encoding = "UTF-8")
public class BaiduSpeechProperties {
    @Value("${baidu.speech.app-id}")
    private String appId;

    @Value("${baidu.speech.api-key}")
    private String apiKey;

    @Value("${baidu.speech.secret-key}")
    private String secretKey;
}
