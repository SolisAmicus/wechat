package com.solisamicus.netty.websocket;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSession {
    // key: user id, value: channels
    private static final Map<String, List<Channel>> multiSession = new ConcurrentHashMap<>();   // 多端

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
        if (channels == null) {
            return new ArrayList<>();
        }
        List<Channel> otherChannels = new ArrayList<>();
        for (Channel channel : channels) {
            if (!channel.id().asLongText().equals(channelId)) {
                otherChannels.add(channel);
            }
        }
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
