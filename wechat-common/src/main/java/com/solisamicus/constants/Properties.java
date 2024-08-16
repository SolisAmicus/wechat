package com.solisamicus.constants;

public class Properties {

    public static final String HEADER_USER_ID = "headerUserId";

    public static final String HEADER_USER_TOKEN = "headerUserToken";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final String TOKEN_USER_PREFIX = "app";

    public static final String MOBILE_SMSCODE_PREFIX = "mobile:smscode";

    public static final Integer CAPTCHA_LENGTH = 6;

    public static final Integer CAPTCHA_VALIDITY_SECONDS = 5; // Verification code validity period //todo

    public static final Integer CAPTCHA_EXPIRATION_SECONDS = 300;

    public static final String REDIS_USER_ALREADY_UPDATE_WECHAT_NUM = "redis_user_already_update_wechat_num";

    public static final String FACE = "face";

    public static final String QRCODE = "qrcode";

    public static final String FRIEND_CIRCLE = "friend circle";

    public static final String IMAGES = "images";

    public static final String BG = "background";

    public static final String COVER = "cover";

    public static final String CHAT = "chat";

    public static final String PHOTO = "photo";

    public static final String VIDEO = "video";

    public static final String VOICE = "voice";

    public static final String NEW_FRIEND_PAGE = "1";

    public static final String NEW_FRIEND_PAGE_SIZE = "10";

    // 是否消息免打扰
    public static final int IS_MSG_IGNORE_NO = 0;

    public static final int IS_MSG_IGNORE_YES = 1;

    // 是否拉黑
    public static final int IS_BLACK_NO = 0;

    public static final int IS_BLACK_YES = 1;

    public static final String REDIS_FRIEND_CIRCLE_LIKED_COUNTS = "friend_circle_liked_counts";

    public static final String REDIS_DOES_USER_LIKE_FRIEND_CIRCLE = "does_user_like_friend_circle";

    public static final String CHAT_MSG_LIST = "chat_msg_list";
}
