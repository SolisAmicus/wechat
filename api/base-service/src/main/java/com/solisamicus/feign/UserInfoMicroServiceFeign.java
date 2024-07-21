package com.solisamicus.feign;

import com.solisamicus.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "main-service")
public interface UserInfoMicroServiceFeign {
    @PostMapping("/userinfo/updateFace")
    GraceJSONResult updateFace(@RequestParam("userId") String userId,
                               @RequestParam("face") String face);

    @PostMapping("/userinfo/updateFriendCircleBg")
    GraceJSONResult updateFriendCircleBg(@RequestParam("userId") String userId,
                                         @RequestParam("friendCircleBg") String friendCircleBg);

    @PostMapping("/userinfo/updateChatBg")
    GraceJSONResult updateChatBg(@RequestParam("userId") String userId,
                                 @RequestParam("chatBg") String chatBg);
}
