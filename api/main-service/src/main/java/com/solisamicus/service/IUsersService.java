package com.solisamicus.service;

import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.ModifyUserBO;

public interface IUsersService {
    void modifyUserInfo(ModifyUserBO UsersBO);

    Users getUserById(String userId);
}
