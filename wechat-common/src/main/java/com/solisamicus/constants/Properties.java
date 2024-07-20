package com.solisamicus.constants;

public class Properties {

    public static final String HEADER_USER_ID = "headerUserId";

    public static final String HEADER_USER_TOKEN = "headerUserToken";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final String TOKEN_USER_PREFIX = "app";

    public static final String MOBILE_SMSCODE_PREFIX = "mobile:smscode"; // Verification code prefix

    public static final Integer CAPTCHA_LENGTH = 6; // Verification code length

    public static final Integer CAPTCHA_VALIDITY_SECONDS = 60; // Verification code validity period

    public static final Integer CAPTCHA_EXPIRATION_SECONDS = 300; //  Verification code expiration period

    public static final String REDIS_USER_ALREADY_UPDATE_WECHAT_NUM = "redis_user_already_update_wechat_num";

    public static  final  String FACE_DIRECTORY = "face"; // Directory name for face images

    public static final String QRCODE_DIRECTORY = "qrcode"; // Directory name for qrcode images
}
