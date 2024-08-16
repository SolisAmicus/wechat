package com.solisamicus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.mapper.ChatMessageMapper;
import com.solisamicus.pojo.ChatMessage;
import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.result.PagedGridResult;
import com.solisamicus.service.IChatMessageService;
import com.solisamicus.utils.PageInfoUtils;
import com.solisamicus.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.solisamicus.constants.Properties.CHAT_MSG_LIST;

@Service
public class ChatMessageServiceImpl implements IChatMessageService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private RedisOperator redis;

    @Transactional
    @Override
    public void saveMsg(ChatMsg chatMsg) {
        ChatMessage message = new ChatMessage();
        BeanUtils.copyProperties(chatMsg, message);
        message.setId(chatMsg.getMsgId());
        String senderId = chatMsg.getSenderId();
        String receiverId = chatMsg.getReceiverId();
        chatMessageMapper.insert(message);
        redis.incrementHash(CHAT_MSG_LIST + ":" + receiverId, senderId, 1);
    }

    @Override
    public PagedGridResult queryChatMsgList(String senderId, String receiverId, Integer page, Integer pageSize) {

        Page<ChatMessage> pageInfo = new Page<>(page, pageSize);
        /**
         * SELECT *
         * FROM chat_message
         * WHERE (sender_id = #{senderId} AND receiver_id = #{receiverId})
         *    OR (sender_id = #{receiverId} AND receiver_id = #{senderId})
         * ORDER BY chat_time DESC;
         */
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<ChatMessage>().
                or(qw -> qw.eq("sender_id", senderId).
                        eq("receiver_id", receiverId)).
                or(qw -> qw.eq("sender_id", receiverId).
                        eq("receiver_id", senderId)).
                orderByDesc("chat_time");
        chatMessageMapper.selectPage(pageInfo, queryWrapper);
        List<ChatMessage> list = pageInfo.getRecords();
        List<ChatMessage> msgList = list.stream().sorted(Comparator.comparing(ChatMessage::getChatTime)).collect(Collectors.toList());
        pageInfo.setRecords(msgList);
        return PageInfoUtils.setterPagedGridPlus(pageInfo);
    }

    @Transactional
    @Override
    public void updateMsgSignRead(String msgId) {
        ChatMessage message = new ChatMessage();
        message.setId(msgId);
        message.setIsRead(true);
        chatMessageMapper.updateById(message);
    }
}
