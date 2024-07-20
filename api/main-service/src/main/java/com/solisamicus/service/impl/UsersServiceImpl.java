package com.solisamicus.service.impl;


import com.solisamicus.exceptions.GraceException;
import com.solisamicus.grace.result.ResponseStatusEnum;
import com.solisamicus.mapper.UsersMapper;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyBO;
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
    public void modifyUserInfo(ModifyBO UsersBO) {
        Users pendingUser = new Users();
        String userId = UsersBO.getUserId();
        String wechatNum = UsersBO.getWechatNum();
        if (StringUtils.isNotBlank(wechatNum)) {
            String isExist = redis.get(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId);
            if (StringUtils.isNotBlank(isExist)) {
                GraceException.display(ResponseStatusEnum.WECHAT_NUM_ALREADY_MODIFIED_ERROR);
            }else{
                redis.set(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId, userId, 31536000);
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
}
