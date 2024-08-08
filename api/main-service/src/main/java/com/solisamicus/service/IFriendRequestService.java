package com.solisamicus.service;

import com.solisamicus.pojo.bo.FriendRequestBO;
import com.solisamicus.result.PagedGridResult;

public interface IFriendRequestService {
    void addNewFriendRequest(FriendRequestBO friendRequestBO);

    PagedGridResult queryNewFriendRequest(String userId, Integer page, Integer pageSize);

    void passNewFriendRequest(String friendRequestId, String friendRemark);
}
