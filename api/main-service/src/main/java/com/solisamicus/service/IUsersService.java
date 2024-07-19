package com.solisamicus.service;

import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyBO;

public interface IUsersService {
    void modifyUserInfo(ModifyBO modifyBO);

    Users getUserById(String userId);
}
