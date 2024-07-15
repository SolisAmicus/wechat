package com.solisamicus.service;

import com.solisamicus.pojo.Users;

public interface IUsersService {
    Users queryMobileIfExist(String mobile);

    Users createUsers(String mobile, String nickname);
}
