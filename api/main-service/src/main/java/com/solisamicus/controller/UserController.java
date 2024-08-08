package com.solisamicus.controller;

import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyUserBO;
import com.solisamicus.pojo.vo.UserVO;
import com.solisamicus.service.IUsersService;
import com.solisamicus.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.solisamicus.constants.Properties.*;
import static com.solisamicus.constants.Symbols.COLON;
import static com.solisamicus.constants.Symbols.DOT;

@RestController
@RequestMapping("userinfo")
public class UserController{
    @Autowired
    private IUsersService usersService;

    @Autowired
    private RedisOperator redis;

    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO UsersBO) {
        usersService.modifyUserInfo(UsersBO);
        UserVO userVO = getUserInfoById(UsersBO.getUserId(), true);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("get")
    public GraceJSONResult get(@RequestParam("userId") String userId) {
        UserVO userVO = getUserInfoById(userId, false);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId,
                                      @RequestParam("face") String face) {
        ModifyUserBO UsersBO = new ModifyUserBO();
        UsersBO.setUserId(userId);
        UsersBO.setFace(face);
        usersService.modifyUserInfo(UsersBO);
        UserVO userVO = getUserInfoById(UsersBO.getUserId(), true);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("updateFriendCircleBg")
    public GraceJSONResult updateFriendCircleBg(@RequestParam("userId") String userId,
                                                @RequestParam("friendCircleBg") String friendCircleBg) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setUserId(userId);
        userBO.setFriendCircleBg(friendCircleBg);
        usersService.modifyUserInfo(userBO);
        UserVO userVO = getUserInfoById(userBO.getUserId(), true);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("updateChatBg")
    public GraceJSONResult updateChatBg(@RequestParam("userId") String userId,
                                        @RequestParam("chatBg") String chatBg) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setUserId(userId);
        userBO.setChatBg(chatBg);
        usersService.modifyUserInfo(userBO);
        UserVO userVO = getUserInfoById(userBO.getUserId(), true);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("queryFriend")
    public GraceJSONResult queryFriend(@RequestParam("queryString")String wechatNumOrMobile, HttpServletRequest request) {
        if (StringUtils.isBlank(wechatNumOrMobile)) {
            return GraceJSONResult.error();
        }
        Users friend = usersService.getUserByWechatNumOrMobile(wechatNumOrMobile);
        if (friend == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FRIEND_NOT_EXIST_ERROR);
        }
        String myId = request.getHeader(HEADER_USER_ID);
        if (myId.equals(friend.getId())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.CAN_NOT_ADD_SELF_FRIEND_ERROR);
        }
        return GraceJSONResult.ok(friend);
    }

    private UserVO getUserInfoById(String userId, boolean needToken) {
        Users latestUser = usersService.getUserById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(latestUser, userVO);
        if (needToken) {
            String uToken = generateUserToken();
            redis.set(tokenRedisKey(userId), uToken);
            userVO.setUserToken(uToken);
        }
        return userVO;
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