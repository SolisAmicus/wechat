package com.solisamicus.enums;

public enum MsgType {

    CONNECT_INIT(0, "初始化(或重连)连接"),
    WORDS(1, "文字/表情"),
    IMAGE(2, "图片"),
    VOICE(3, "语音"),
    VIDEO(4, "视频"),

    SIGNED(8, "消息签收"),
    KEEPALIVE(9, "保持心跳"),
    heart(10, "拉取好友");

    public final Integer type;
    public final String content;

    MsgType(Integer type, String content){
        this.type = type;
        this.content = content;
    }
}
