package com.solisamicus.controller;

import com.solisamicus.base.BaseInfoProperties;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyBO;
import com.solisamicus.pojo.vo.UsersVO;
import com.solisamicus.service.IUsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("userinfo")
public class UserController extends BaseInfoProperties {
    @Autowired
    private IUsersService usersService;

    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyBO modifyBO) {
        usersService.modifyUserInfo(modifyBO);
        return GraceJSONResult.ok(getUserInfoById(modifyBO.getUserId(),true));
    }

    @PostMapping("get")
    public GraceJSONResult get(@RequestParam String userId) {
        return GraceJSONResult.ok(getUserInfoById(userId, false));
    }


    private UsersVO getUserInfoById(String userId, boolean needToken) {
        Users latestUser = usersService.getUserById(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);
        if (needToken) {
            String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
            redis.set(REDIS_USER_TOKEN + ":" + userId, uToken);
            usersVO.setUserToken(uToken);
        }
        return usersVO;
    }
}