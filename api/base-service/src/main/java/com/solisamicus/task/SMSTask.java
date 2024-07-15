package com.solisamicus.task;

import com.solisamicus.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SMSTask {
    @Autowired
    private SMSUtils smsUtils;

    @Async
    public void sendSMSAsync(String mobile, String captcha){
        // smsUtils.sendSMS(mobile, captcha);
        log.info("验证码为: {}", captcha);
    }
}
