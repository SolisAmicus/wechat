package com.solisamicus.enums;

public enum FriendRequestVerifyStatus {
    WAIT(0, "待审核"),
    SUCCESS(1, "已添加"),
    EXPIRE(2, "已过期");

    public final Integer type;

    public final String value;

    FriendRequestVerifyStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
