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

import static com.solisamicus.constants.Symbols.COLON;
import static com.solisamicus.constants.Symbols.DOT;

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
    public GraceJSONResult get(@RequestParam("userId") String userId) {
        return GraceJSONResult.ok(getUserInfoById(userId, false));
    }


    private UsersVO getUserInfoById(String userId, boolean needToken) {
        Users latestUser = usersService.getUserById(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);
        if (needToken) {
            String uToken = generateUserToken();
            redis.set(tokenRedisKey(userId), uToken);
            usersVO.setUserToken(uToken);
        }
        return usersVO;
    }

    /**
     * Generate credential redis key
     *
     * @param value
     * @return redis_user_token:{value}
     */
    private String tokenRedisKey(Object value) {
        return String.format("%s%s%s", REDIS_USER_TOKEN, COLON, value);
    }

    /**
     * Generate credential
     *
     * @return app.{UUID}
     */
    private String generateUserToken() {
        return String.format("%s%s%s", TOKEN_USER_PREFIX, DOT, UUID.randomUUID());
    }
}