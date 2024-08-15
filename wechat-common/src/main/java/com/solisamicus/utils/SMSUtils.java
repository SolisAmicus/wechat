package com.solisamicus.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.models.*;
import com.aliyun.sdk.service.dysmsapi20170525.*;
import com.google.gson.Gson;
import com.solisamicus.config.AliyunSMSProperties;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Component
public class SMSUtils {
    @Autowired
    private AliyunSMSProperties aliyunSMSProperties;

    public void sendSMS(String mobile, String captcha){
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(aliyunSMSProperties.getAccessKeyId())
                .accessKeySecret(aliyunSMSProperties.getAccessKeySecret())
                .build());
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create().setEndpointOverride(aliyunSMSProperties.getEndpoint())
                )
                .build();
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(aliyunSMSProperties.getSignName())
                .templateCode(aliyunSMSProperties.getTemplateCode())
                .phoneNumbers(mobile)
                .templateParam("{\"code\":\"" + captcha + "\"}")
                .build();
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println(new Gson().toJson(resp));
        client.close();
    }
}
