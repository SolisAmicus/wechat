package com.solisamicus.netty.websocket;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.solisamicus.enums.MsgType;
import com.solisamicus.grace.result.GraceJSONResult;
import com.solisamicus.netty.rabbitmq.RabbitMQPublisher;
import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.pojo.netty.DataContent;
import com.solisamicus.utils.JsonUtils;
import com.solisamicus.utils.LocalDateUtils;
import com.solisamicus.utils.OKHTTPUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) {
        String content = textWebSocketFrame.text();
        DataContent data = JsonUtils.jsonToPojo(content, DataContent.class);
        assert data != null;
        ChatMsg chatMsg = data.getChatMsg();

        String senderId = chatMsg.getSenderId();
        String receiverId = chatMsg.getReceiverId();
        Integer msgType = chatMsg.getMsgType();

        // 黑名单
        GraceJSONResult result = OKHTTPUtils.get("http://127.0.0.1:1000/friendship/isBlack?friendId1st=" + receiverId + "&friendId2nd=" + senderId);
        boolean isBlack = (Boolean) result.getData();
        if (isBlack) {
            return;
        }

        chatMsg.setChatTime(LocalDateTime.now());

        Channel channel = channelHandlerContext.channel();
        String channelId = channel.id().asLongText();

        if (Objects.equals(msgType, MsgType.CONNECT_INIT.type)) {
            UserSession.putMultiChannels(senderId, channel);
            UserSession.putUserChannel(channelId, senderId);
            UserSession.printSessions();
        } else if (MsgType.WORDS.type.equals(msgType)
                || MsgType.IMAGE.type.equals(msgType)
                || MsgType.VIDEO.type.equals(msgType)
                || MsgType.VOICE.type.equals(msgType)) {
            chatMsg.setMsgId(IdWorker.getIdStr());
            List<Channel> receiverChannels = UserSession.getMultiChannels(receiverId);
            if (Objects.isNull(receiverChannels) || receiverChannels.isEmpty()) {
                chatMsg.setIsReceiverOnLine(false);
            } else {
                chatMsg.setIsReceiverOnLine(true);
                // 发送消息，同一用户不同设备
                for (Channel c : receiverChannels) {
                    Channel findChannel = clients.find(c.id());
                    if (Objects.nonNull(findChannel)) {
                        if (MsgType.VOICE.type.equals(msgType)) chatMsg.setIsRead(false);
                        data.setChatMsg(chatMsg);
                        data.setChatTime(LocalDateUtils.format(chatMsg.getChatTime(), LocalDateUtils.DATETIME_PATTERN_2));
                        findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(data)));
                    }
                }
            }
            try {
                RabbitMQPublisher.sendMsgToSave(chatMsg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<Channel> myOtherMultiChannels = UserSession.getMyOtherMultiChannels(senderId, channelId);
        // 同步消息，同一用户不同设备
        for (Channel c : myOtherMultiChannels) {
            Channel findChannel = clients.find(c.id());
            if (Objects.nonNull(findChannel)) {
                data.setChatMsg(chatMsg);
                data.setChatTime(LocalDateUtils.format(chatMsg.getChatTime(), LocalDateUtils.DATETIME_PATTERN_2));
                findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(data)));
            }
        }
        System.out.printf("Received data: %s\n", content);
        String currentChannelId = channelHandlerContext.channel().id().asLongText();
        System.out.printf("Current channel id: %s\n", currentChannelId);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        System.out.printf("* Add channel: %s\n", currentChannelId);
        clients.add(currentChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        System.out.printf("* Remove channel: %s\n", currentChannelId);
        String userChannels = UserSession.getUserChannels(currentChannelId);
        UserSession.removeUselessChannels(userChannels, currentChannel);
        clients.remove(currentChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        System.out.printf("* Exception occurred on: %s\n", currentChannelId);
        String userChannels = UserSession.getUserChannels(currentChannelId);
        UserSession.removeUselessChannels(userChannels, currentChannel);
        ctx.channel().close();
        clients.remove(currentChannel);
    }
}
