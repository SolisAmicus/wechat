package com.solisamicus.netty.websocket;

import com.solisamicus.enums.MsgType;
import com.solisamicus.pojo.netty.ChatMsg;
import com.solisamicus.pojo.netty.DataContent;
import com.solisamicus.utils.JsonUtils;
import com.solisamicus.utils.LocalDateUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserSession {
    // key: user id, value: channels
    private static final Map<String, List<Channel>> multiSession = new ConcurrentHashMap<>();

    // key: channel id, value: user id
    private static final Map<String, String> channelUserIDRelation = new ConcurrentHashMap<>();

    public static void putMultiChannels(String userId, Channel channel) {
        List<Channel> channels = multiSession.computeIfAbsent(userId, k -> new ArrayList<>());
        channels.add(channel);
    }

    public static List<Channel> getMultiChannels(String userId) {
        return multiSession.get(userId);
    }

    public static List<Channel> getMyOtherMultiChannels(String userId, String channelId) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.isEmpty()) {
            return null;
        }
        List<Channel> otherChannels = new ArrayList<>();
        for (Channel channel : channels) {
            if (!channel.id().asLongText().equals(channelId)) {
                otherChannels.add(channel);
            }
        }
        System.out.println(otherChannels);
        return otherChannels;
    }

    public static void removeUselessChannels(String userId, Channel channel) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.isEmpty()) {
            return;
        }
        channels.removeIf(ch -> ch.equals(channel));
    }

    public static void putUserChannel(String channelId, String userId) {
        channelUserIDRelation.put(channelId, userId);
    }

    public static String getUserChannels(String channelId) {
        return channelUserIDRelation.get(channelId);
    }

    public static void sendToTarget(List<Channel> receiverChannels, DataContent data) {
        ChannelGroup clients = WebSocketHandler.clients;
        ChatMsg chatMsg = data.getChatMsg();
        if (Objects.isNull(receiverChannels) || receiverChannels.isEmpty()) {
            chatMsg.setIsReceiverOnLine(false);
        } else {
            chatMsg.setIsReceiverOnLine(true);
            for (Channel c : receiverChannels) {
                Channel findChannel = clients.find(c.id());
                if (Objects.nonNull(findChannel)) {
                    if (MsgType.VOICE.type.equals(chatMsg.getMsgType())) {
                        chatMsg.setIsRead(false);
                    }
                    data.setChatMsg(chatMsg);
                    findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(data)));
                }
            }
        }
    }

    public static void printSessions() {
        System.out.println("=".repeat(20));
        System.out.println("User Sessions (multiSession):");
        for (Map.Entry<String, List<Channel>> entry : multiSession.entrySet()) {
            System.out.println("User ID: " + entry.getKey());
            List<String> channelIds = new ArrayList<>();
            for (Channel channel : entry.getValue()) {
                channelIds.add(channel.id().asLongText());
            }
            System.out.println("Channels" + channelIds);
        }

        System.out.println("\nChannel-User Relationships (channelUserRelationship):");
        for (Map.Entry<String, String> entry : channelUserIDRelation.entrySet()) {
            System.out.println("Channel: " + entry.getKey());
            System.out.println("User ID: " + entry.getValue());
        }
        System.out.println("=".repeat(20));
    }
}
