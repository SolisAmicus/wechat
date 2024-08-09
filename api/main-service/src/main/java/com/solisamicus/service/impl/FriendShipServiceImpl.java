package com.solisamicus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solisamicus.enums.Black;
import com.solisamicus.mapper.FriendShipMapper;
import com.solisamicus.mapper.FriendShipMapperCustom;
import com.solisamicus.pojo.FriendShip;
import com.solisamicus.pojo.vo.ContactsVO;
import com.solisamicus.service.IFriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FriendShipServiceImpl implements IFriendShipService {
    @Autowired
    private FriendShipMapper friendShipMapper;

    @Autowired
    private FriendShipMapperCustom friendShipMapperCustom;

    @Override
    public FriendShip getFriendShip(String myId, String friendId) {
        QueryWrapper<FriendShip> queryWrapper = new QueryWrapper<FriendShip>()
                .eq("my_id", myId)
                .eq("friend_id", friendId);
        return friendShipMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ContactsVO> queryMyFriends(String myId, boolean needBlack) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        map.put("needBlack", needBlack);
        return friendShipMapperCustom.queryMyFriends(map);
    }

    @Transactional
    @Override
    public void updateFriendRemark(String myId, String friendId, String friendRemark) {
        QueryWrapper<FriendShip> updateWrapper = new QueryWrapper<FriendShip>().
                eq("my_id", myId).
                eq("friend_id", friendId);
        FriendShip friendship = new FriendShip();
        friendship.setFriendRemark(friendRemark);
        friendship.setUpdatedTime(LocalDateTime.now());
        friendShipMapper.update(friendship, updateWrapper);
    }

    @Transactional
    @Override
    public void updateBlackList(String myId, String friendId, Black black) {
        QueryWrapper<FriendShip> updateWrapper = new QueryWrapper<FriendShip>().
                eq("my_id", myId).
                eq("friend_id", friendId);
        FriendShip friendship = new FriendShip();
        friendship.setIsBlack(black.type);
        friendship.setUpdatedTime(LocalDateTime.now());
        friendShipMapper.update(friendship, updateWrapper);
    }

    @Transactional
    @Override
    public void delete(String myId, String friendId) {
        QueryWrapper<FriendShip> deleteWrapper1 = new QueryWrapper<FriendShip>().
                eq("my_id", myId).
                eq("friend_id", friendId);
        friendShipMapper.delete(deleteWrapper1);
        QueryWrapper<FriendShip> deleteWrapper2 = new QueryWrapper<FriendShip>().
                eq("my_id", friendId).
                eq("friend_id", myId);
        friendShipMapper.delete(deleteWrapper2);
    }
}
