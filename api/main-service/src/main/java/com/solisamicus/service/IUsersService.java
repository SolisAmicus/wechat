package com.solisamicus.service;

import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyBO;

public interface IUsersService {
    void modifyUserInfo(ModifyBO UsersBO);

    Users getUserById(String userId);
}
