package com.solisamicus.controller;

import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.LoginBO;
import com.solisamicus.pojo.bo.RegisterBO;
import com.solisamicus.pojo.vo.UserVO;
import com.solisamicus.service.IUsersService;
import com.solisamicus.task.SMSTask;
import com.solisamicus.utils.IPUtils;
import com.solisamicus.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.solisamicus.constants.Properties.*;
import static com.solisamicus.constants.Symbols.COLON;
import static com.solisamicus.constants.Symbols.DOT;

@RestController
@RequestMapping("passport")
public class PassPortController{
    @Autowired
    private SMSTask smsTask;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private IUsersService usersService;

    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }

        String ip = IPUtils.getRequestIp(request);
        redis.setIfAbsentWithTTL(captchaRedisKey(ip), mobile, CAPTCHA_VALIDITY_SECONDS); // <keyForIp, mobile>

        String captcha = generateCaptcha();
        smsTask.sendSMSAsync(mobile, captcha);
        redis.set(captchaRedisKey(mobile), captcha, CAPTCHA_EXPIRATION_SECONDS); //  <keyForMobile, captcha>

        return GraceJSONResult.ok();
    }

    @PostMapping("regist")
    public GraceJSONResult register(@RequestBody @Valid RegisterBO registerBO) {
        String mobile = registerBO.getMobile();
        String captcha = registerBO.getSmsCode();
        String nickname = registerBO.getNickname();

        String redisKey = captchaRedisKey(mobile);
        String storedCaptcha = redis.get(redisKey);

        if (StringUtils.isBlank(storedCaptcha) || !storedCaptcha.equalsIgnoreCase(captcha)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        redis.del(redisKey);

        Users user = usersService.queryMobileIfExist(mobile);
        if (user != null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ALREADY_EXIST_ERROR);
        }
        user = usersService.createUsers(mobile, nickname);

        String uToken = generateUserToken();
        redis.set(tokenRedisKey(user.getId()), uToken);
        // String keyForUserDevice = tokenRedisKey(uToken);
        // redis.set(keyForUserDevice,uId);

        UserVO userVO = convertToUserVO(user, uToken);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("login")
    public GraceJSONResult login(@RequestBody @Valid LoginBO loginBO) {
        String mobile = loginBO.getMobile();
        String captcha = loginBO.getSmsCode();

        String redisKey = captchaRedisKey(mobile);
        String storedCaptcha = redis.get(redisKey);

        if (StringUtils.isBlank(storedCaptcha) || !storedCaptcha.equalsIgnoreCase(captcha)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        redis.del(redisKey);

        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        String uToken = generateUserToken();
        redis.set(tokenRedisKey(user.getId()), uToken);

        UserVO userVO = convertToUserVO(user, uToken);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("registOrLogin")
    public GraceJSONResult registerOrLogin(@RequestBody @Valid RegisterBO registerBO) {
        String mobile = registerBO.getMobile();
        String captcha = registerBO.getSmsCode();
        String nickname = registerBO.getNickname();

        String redisKey = captchaRedisKey(mobile);
        String storedCaptcha = redis.get(redisKey);

        if (StringUtils.isBlank(storedCaptcha) || !storedCaptcha.equalsIgnoreCase(captcha)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        redis.del(redisKey);

        Users user = usersService.queryMobileIfExist(mobile);

        if (user == null) {
            user = usersService.createUsers(mobile, nickname);
        }

        String uToken = generateUserToken();
        redis.set(tokenRedisKey(user.getId()), uToken);


        UserVO userVO = convertToUserVO(user, uToken);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam("userId") String userId) {
        redis.del(tokenRedisKey(userId));
        return GraceJSONResult.ok();
    }


    /**
     * Generate verification code
     *
     * @return Returns a random verification code of [0000000,999999]
     */
    private String generateCaptcha() {
        int randomNumber = (int) (Math.random() * Math.pow(10, CAPTCHA_LENGTH));
        return String.format("%0" + CAPTCHA_LENGTH + "d", randomNumber);
    }

    /**
     * Generate verification code redis key
     *
     * @param value
     * @return mobile:smscode:{value}
     */
    private String captchaRedisKey(Object value) {
        return String.format("%s%s%s", MOBILE_SMSCODE_PREFIX, COLON, value);
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

    /**
     * Convert user object to user view object
     *
     * @param user user object
     * @param userToken user view object
     * @return converted user view object
     */
    private UserVO convertToUserVO(Users user, String userToken) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(userToken);
        return userVO;
    }
}
