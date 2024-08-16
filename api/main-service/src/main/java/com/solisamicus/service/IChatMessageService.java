package com.solisamicus.service;

import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.result.PagedGridResult;

public interface IChatMessageService {
    void saveMsg(ChatMsg chatMsg);

    PagedGridResult queryChatMsgList(String senderId, String receiverId, Integer page, Integer pageSize);

    void updateMsgSignRead(String msgId);
}
