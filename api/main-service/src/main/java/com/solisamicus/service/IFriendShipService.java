package com.solisamicus.service;

import com.solisamicus.enums.Black;
import com.solisamicus.pojo.FriendShip;
import com.solisamicus.pojo.vo.ContactsVO;

import java.util.List;

public interface IFriendShipService {
    FriendShip getFriendShip(String myId, String friendId);

    List<ContactsVO> queryMyFriends(String myId, boolean needBlack);

    void updateFriendRemark(String myId, String friendId, String friendRemark);

    void updateBlackList(String myId, String friendId, Black black);

    void delete(String myId, String friendId);
}
