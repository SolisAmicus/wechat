package com.solisamicus.service;

import com.solisamicus.pojo.FriendCircleLiked;
import com.solisamicus.pojo.bo.FriendCircleBO;
import com.solisamicus.result.PagedGridResult;

import java.util.List;

public interface IFriendCircleService {
    void publish(FriendCircleBO friendCircleBO, String userId);

    PagedGridResult queryFriendCircles(String userId, Integer page, Integer pageSize);

    void like(String friendCircleId, String userId);

    void unlike(String friendCircleId, String userId);

    List<FriendCircleLiked> queryLikedFriends(String friendCircleId);

    boolean doILike(String friendCircleId, String userId);

    void delete(String friendCircleId, String userId);
}
