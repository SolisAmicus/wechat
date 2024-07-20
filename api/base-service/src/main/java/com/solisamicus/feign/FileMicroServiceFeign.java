package com.solisamicus.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "file-service")
public interface FileMicroServiceFeign {

    @PostMapping("/file/generatorQrCode")
    String generatorQrCode(@RequestParam("userId") String userId, @RequestParam("wechatNumber") String wechatNumber);
}
