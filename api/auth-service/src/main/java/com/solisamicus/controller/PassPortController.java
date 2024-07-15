package com.solisamicus.controller;

import com.solisamicus.base.BaseInfoProperties;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.LoginBO;
import com.solisamicus.pojo.bo.RegisterBO;
import com.solisamicus.pojo.vo.UsersVO;
import com.solisamicus.service.IUsersService;
import com.solisamicus.task.SMSTask;
import com.solisamicus.utils.IPUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("passport")
public class PassPortController extends BaseInfoProperties {
    @Autowired
    private SMSTask smsTask;

    @Autowired
    private IUsersService usersService;

    // 127.0.0.1:8888/passport/getSMSCode
    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }
        redis.setIfAbsentWithTTL(MOBILE_SMSCODE + ":" + IPUtil.getRequestIp(request), mobile, 60); // 有效验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        smsTask.sendSMSAsync(mobile, captcha);
        redis.setOrUpdateWithTTL(MOBILE_SMSCODE + ":" + mobile, captcha, 5 * 60); // 过期验证码
        return GraceJSONResult.ok();
    }

    @PostMapping("regist")
    public GraceJSONResult register(@RequestBody @Valid RegisterBO registerBO) {
        String mobile = registerBO.getMobile();
        String captcha = registerBO.getSmsCode();
        String nickname = registerBO.getNickname();
        String rCaptcha = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(rCaptcha) || !rCaptcha.equalsIgnoreCase(captcha)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            user = usersService.createUsers(mobile, nickname);
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ALREADY_EXIST_ERROR);
        }
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        return GraceJSONResult.ok(user);
    }

    @PostMapping("login")
    public GraceJSONResult login(@RequestBody @Valid LoginBO loginBO) {
        String mobile = loginBO.getMobile();
        String captcha = loginBO.getSmsCode();
        String rCaptcha = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(rCaptcha) || !rCaptcha.equalsIgnoreCase(captcha)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
        redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);
        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("registOrLogin")
    public GraceJSONResult registerOrLogin(@RequestBody @Valid RegisterBO registerBO) {
        String mobile = registerBO.getMobile();
        String captcha = registerBO.getSmsCode();
        String nickname = registerBO.getNickname();
        String rCaptcha = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(rCaptcha) || !rCaptcha.equalsIgnoreCase(captcha)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            user = usersService.createUsers(mobile, nickname);
        }
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID().toString();
        redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);
        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId) {
        redis.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }
}
