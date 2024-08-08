package com.solisamicus.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solisamicus.exceptions.GraceException;
import com.solisamicus.feign.FileMicroServiceFeign;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.mapper.UsersMapper;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyUserBO;
import com.solisamicus.service.IUsersService;
import com.solisamicus.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.solisamicus.constants.Properties.REDIS_USER_ALREADY_UPDATE_WECHAT_NUM;

@Service
public class UsersServiceImpl implements IUsersService {
    @Autowired
    private RedisOperator redis;

    @Autowired
    private UsersMapper usersMapper;

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO UsersBO) {
        Users pendingUser = new Users();
        String userId = UsersBO.getUserId();
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }
        String wechatNum = UsersBO.getWechatNum();
        if (StringUtils.isNotBlank(wechatNum)) {
            String isExist = redis.get(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId);
            if (StringUtils.isNotBlank(isExist)) {
                GraceException.display(ResponseStatusEnum.WECHAT_NUM_ALREADY_MODIFIED_ERROR);
            } else {
                redis.set(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId, userId, 31536000);
                pendingUser.setWechatNumImg(getQrCodeUrl(userId,wechatNum));
            }
        }
        pendingUser.setId(userId);
        BeanUtils.copyProperties(UsersBO, pendingUser);
        pendingUser.setUpdatedTime(LocalDateTime.now());
        usersMapper.updateById(pendingUser);
    }

    @Override
    public Users getUserById(String userId) {
        return usersMapper.selectById(userId);
    }

    @Override
    public Users getUserByWechatNumOrMobile(String wechatNumOrMobile) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<Users>()
                .eq("wechat_num", wechatNumOrMobile)
                .or()
                .eq("mobile", wechatNumOrMobile);
        return usersMapper.selectOne(queryWrapper);
    }

    @Autowired
    private FileMicroServiceFeign fileMicroServiceFeign;

    private String getQrCodeUrl(String userId, String wechatNumber) {
        return fileMicroServiceFeign.generatorQrCode(userId, wechatNumber);
    }
}
