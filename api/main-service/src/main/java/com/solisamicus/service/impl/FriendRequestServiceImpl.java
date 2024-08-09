package com.solisamicus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.enums.FriendRequestVerifyStatus;
import com.solisamicus.mapper.FriendRequestMapper;
import com.solisamicus.mapper.FriendRequestMapperCustom;
import com.solisamicus.mapper.FriendShipMapper;
import com.solisamicus.pojo.FriendRequest;
import com.solisamicus.pojo.FriendShip;
import com.solisamicus.pojo.bo.FriendRequestBO;
import com.solisamicus.pojo.vo.FriendRequestVO;
import com.solisamicus.result.PagedGridResult;
import com.solisamicus.service.IFriendRequestService;
import com.solisamicus.utils.PageInfoUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.solisamicus.constants.Properties.IS_BLACK_NO;
import static com.solisamicus.constants.Properties.IS_MSG_IGNORE_NO;

@Service
public class FriendRequestServiceImpl implements IFriendRequestService {
    @Autowired
    private FriendRequestMapper friendRequestMapper;
    @Autowired
    private FriendRequestMapperCustom friendRequestMapperCustom;
    @Autowired
    private FriendShipMapper friendShipMapper;

    @Override
    public void addNewFriendRequest(FriendRequestBO friendRequestBO) {
        QueryWrapper<FriendRequest> deleteWrapper = new QueryWrapper<FriendRequest>()
                .eq("my_id", friendRequestBO.getMyId())
                .eq("friend_id", friendRequestBO.getFriendId());
        friendRequestMapper.delete(deleteWrapper);
        FriendRequest pendingFriendRequest = new FriendRequest();
        BeanUtils.copyProperties(friendRequestBO, pendingFriendRequest);
        pendingFriendRequest.setVerifyStatus(FriendRequestVerifyStatus.WAIT.type);
        pendingFriendRequest.setRequestTime(LocalDateTime.now());
        friendRequestMapper.insert(pendingFriendRequest);
    }

    @Override
    public PagedGridResult queryNewFriendRequest(String userId,
                                                     Integer page,
                                                     Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mySelfId", userId);
        Page<FriendRequestVO> pageInfo = new Page<>(page, pageSize);
        friendRequestMapperCustom.queryNewFriendRequestList(pageInfo, paramMap);
        return PageInfoUtils.setterPagedGridPlus(pageInfo);
    }

    @Override
    public void passNewFriendRequest(String friendRequestId, String friendRemark) {
        FriendRequest friendRequest = friendRequestMapper.selectById(friendRequestId);
        String mySelfId = friendRequest.getFriendId();
        String friendId = friendRequest.getMyId();
        LocalDateTime now = LocalDateTime.now();

        FriendShip friendShipO = new FriendShip();
        friendShipO.setMyId(mySelfId);
        friendShipO.setFriendId(friendId);
        friendShipO.setFriendRemark(friendRemark);
        friendShipO.setChatBg("");
        friendShipO.setIsMsgIgnore(IS_MSG_IGNORE_NO);
        friendShipO.setIsBlack(IS_BLACK_NO);
        friendShipO.setCreatedTime(now);
        friendShipO.setUpdatedTime(now);

        FriendShip friendShipT = new FriendShip();
        friendShipT.setMyId(friendId);
        friendShipT.setFriendId(mySelfId);
        friendShipT.setFriendRemark(friendRequest.getFriendRemark());
        friendShipT.setChatBg("");
        friendShipT.setIsMsgIgnore(IS_MSG_IGNORE_NO);
        friendShipT.setIsBlack(IS_BLACK_NO);
        friendShipT.setCreatedTime(now);
        friendShipT.setUpdatedTime(now);

        friendShipMapper.insert(friendShipO);
        friendShipMapper.insert(friendShipT);

        friendRequest.setVerifyStatus(FriendRequestVerifyStatus.SUCCESS.type);
        friendRequestMapper.updateById(friendRequest);

        QueryWrapper<FriendRequest> updateWrapper = new QueryWrapper<FriendRequest>()
                .eq("my_id", friendId)
                .eq("friend_id", mySelfId);
        FriendRequest requestOpposite = new FriendRequest();
        requestOpposite.setVerifyStatus(FriendRequestVerifyStatus.SUCCESS.type);
        friendRequestMapper.update(requestOpposite, updateWrapper);
    }
}
