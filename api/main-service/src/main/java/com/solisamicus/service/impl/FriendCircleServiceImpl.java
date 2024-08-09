package com.solisamicus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.mapper.FriendCircleLikedMapper;
import com.solisamicus.mapper.FriendCircleMapper;
import com.solisamicus.mapper.FriendCircleMapperCustom;
import com.solisamicus.mapper.UsersMapper;
import com.solisamicus.pojo.FriendCircle;
import com.solisamicus.pojo.FriendCircleLiked;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.FriendCircleBO;
import com.solisamicus.pojo.vo.FriendCircleVO;
import com.solisamicus.result.PagedGridResult;
import com.solisamicus.service.IFriendCircleService;
import com.solisamicus.utils.PageInfoUtils;
import com.solisamicus.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.solisamicus.constants.Properties.REDIS_DOES_USER_LIKE_FRIEND_CIRCLE;
import static com.solisamicus.constants.Properties.REDIS_FRIEND_CIRCLE_LIKED_COUNTS;

@Service
public class FriendCircleServiceImpl implements IFriendCircleService {

    @Autowired
    private FriendCircleMapper friendCircleMapper;

    @Autowired
    private FriendCircleMapperCustom friendCircleMapperCustom;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private FriendCircleLikedMapper friendCircleLikedMapper;

    @Autowired
    private RedisOperator redis;

    @Transactional
    @Override
    public void publish(FriendCircleBO friendCircleBO, String userId) {
        friendCircleBO.setUserId(userId);
        friendCircleBO.setPublishTime(LocalDateTime.now());
        FriendCircle pendingFriendCircle = new FriendCircle();
        BeanUtils.copyProperties(friendCircleBO, pendingFriendCircle);
        friendCircleMapper.insert(pendingFriendCircle);
    }

    @Override
    public PagedGridResult queryFriendCircles(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        Page<FriendCircleVO> pageInfo = new Page<>(page, pageSize);
        friendCircleMapperCustom.queryFriendCircleList(pageInfo, map);
        return PageInfoUtils.setterPagedGridPlus(pageInfo);
    }

    @Transactional
    @Override
    public void like(String friendCircleId, String userId) {
        FriendCircle friendCircle = friendCircleMapper.selectById(friendCircleId);
        Users users = usersMapper.selectById(userId);
        FriendCircleLiked friendCircleLiked = new FriendCircleLiked();
        friendCircleLiked.setBelongUserId(friendCircle.getUserId());
        friendCircleLiked.setFriendCircleId(friendCircle.getId());
        friendCircleLiked.setLikedUserId(users.getId());
        friendCircleLiked.setLikedUserName(users.getNickname());
        friendCircleLiked.setCreatedTime(LocalDateTime.now());
        friendCircleLikedMapper.insert(friendCircleLiked);
        redis.increment(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);
        redis.setIfAbsent(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId, userId);
    }

    @Transactional
    @Override
    public void unlike(String friendCircleId, String userId) {
        QueryWrapper<FriendCircleLiked> deleteWrapper = new QueryWrapper<FriendCircleLiked>()
                .eq("friend_circle_id", friendCircleId)
                .eq("liked_user_id", userId);
        friendCircleLikedMapper.delete(deleteWrapper);
        redis.decrement(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);
        redis.del(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId);
    }

    @Override
    public List<FriendCircleLiked> queryLikedFriends(String friendCircleId) {
        QueryWrapper<FriendCircleLiked> queryWrapper = new QueryWrapper<FriendCircleLiked>()
                .eq("friend_circle_id", friendCircleId);
        return friendCircleLikedMapper.selectList(queryWrapper);
    }

    @Override
    public boolean doILike(String friendCircleId, String userId) {
        return StringUtils.isNotBlank(redis.get(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId));
    }

    @Transactional
    @Override
    public void delete(String friendCircleId, String userId) {
        QueryWrapper<FriendCircle> deleteWrapper = new QueryWrapper<FriendCircle>().
                eq("id", friendCircleId).
                eq("user_id", userId);
        friendCircleMapper.delete(deleteWrapper);
    }
}
