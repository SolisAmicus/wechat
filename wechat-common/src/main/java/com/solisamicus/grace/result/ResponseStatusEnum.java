package com.solisamicus.grace.result;

public enum ResponseStatusEnum {
    SUCCESS(200, true, "操作成功！"),
    FAILED(500, false, "操作失败！"),
    UN_LOGIN(501, false, "请登录后再继续操作！"),
    TICKET_INVALID(502, false, "会话失效，请重新登录！"),
    USER_ALREADY_EXIST_ERROR(5021, false, "该用户已存在，不可重复注册！"),
    USER_ISNOT_EXIST_ERROR(5023, false, "该用户不存在，请前往注册！"),
    WECHAT_NUM_ALREADY_MODIFIED_ERROR(5024, false, "微信号已被修改，请等待1年后再修改！"),
    CAN_NOT_ADD_SELF_FRIEND_ERROR(5025, false, "无法添加自己为好友！"),
    FRIEND_NOT_EXIST_ERROR(5026, false, "好友不存在！"),
    NO_AUTH(503, false, "您的权限不足，无法继续操作！"),
    MOBILE_ERROR(504, false, "短信发送失败，请稍后重试！"),
    SMS_NEED_WAIT_ERROR(505, false, "短信发送太快啦~请稍后再试！"),
    SMS_CODE_ERROR(506, false, "验证码过期或不匹配，请稍后再试！"),
    USER_FROZEN(507, false, "用户已被冻结，请联系管理员！"),
    USER_UPDATE_ERROR(508, false, "用户信息更新失败，请联系管理员！"),
    USER_INACTIVE_ERROR(509, false, "请前往[账号设置]修改信息激活后再进行后续操作！"),
    USER_INFO_UPDATED_ERROR(5091, false, "用户信息修改失败！"),
    USER_INFO_UPDATED_NICKNAME_EXIST_ERROR(5092, false, "昵称已经存在！"),
    USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR(5092, false, "慕课号已经存在！"),
    USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR(5092, false, "慕课号无法修改！"),
    FILE_UPLOAD_NULL_ERROR(510, false, "文件不能为空，请选择一个文件再上传！"),
    FILE_UPLOAD_FAILD(511, false, "文件上传失败！"),
    FILE_FORMATTER_FAILD(512, false, "文件图片格式不支持！"),
    FILE_MAX_SIZE_500KB_ERROR(5131, false, "仅支持500kb大小以下的文件上传！"),
    FILE_MAX_SIZE_2MB_ERROR(5132, false, "仅支持2MB大小以下的文件上传！"),
    FILE_MAX_SIZE_8MB_ERROR(5133, false, "体验版仅支持8MB以下的文件上传！"),
    FILE_MAX_SIZE_100MB_ERROR(5134, false, "仅支持100MB大小以下的文件上传！"),
    FILE_NOT_EXIST_ERROR(514, false, "你所查看的文件不存在！"),
    USER_STATUS_ERROR(515, false, "用户状态参数出错！"),
    USER_NOT_EXIST_ERROR(516, false, "用户不存在！"),
    USER_PARAMS_ERROR(517, false, "用户请求参数出错！"),
    USER_REGISTER_ERROR(518, false, "用户注册失败，请重试！"),
    SYSTEM_INDEX_OUT_OF_BOUNDS(541, false, "系统错误，数组越界！"),
    SYSTEM_ARITHMETIC_BY_ZERO(542, false, "系统错误，无法除零！"),
    SYSTEM_NULL_POINTER(543, false, "系统错误，空指针！"),
    SYSTEM_NUMBER_FORMAT(544, false, "系统错误，数字转换异常！"),
    SYSTEM_PARSE(545, false, "系统错误，解析异常！"),
    SYSTEM_IO(546, false, "系统错误，IO输入输出异常！"),
    SYSTEM_FILE_NOT_FOUND(547, false, "系统错误，文件未找到！"),
    SYSTEM_CLASS_CAST(548, false, "系统错误，类型强制转换错误！"),
    SYSTEM_PARSER_ERROR(549, false, "系统错误，解析出错！"),
    SYSTEM_DATE_PARSER_ERROR(550, false, "系统错误，日期解析出错！"),
    SYSTEM_NO_EXPIRE_ERROR(552, false, "系统错误，缺少过期时间！"),
    HTTP_URL_CONNECT_ERROR(551, false, "目标地址无法请求！"),
    ADMIN_USERNAME_NULL_ERROR(561, false, "管理员登录名不能为空！"),
    ADMIN_USERNAME_EXIST_ERROR(562, false, "管理员账户名已存在！"),
    ADMIN_NAME_NULL_ERROR(563, false, "管理员负责人不能为空！"),
    ADMIN_PASSWORD_ERROR(564, false, "密码不能为空或者两次输入不一致！"),
    ADMIN_CREATE_ERROR(565, false, "添加管理员失败！"),
    ADMIN_PASSWORD_NULL_ERROR(566, false, "密码不能为空！"),
    ADMIN_LOGIN_ERROR(567, false, "管理员不存在或密码不正确！"),
    ADMIN_FACE_NULL_ERROR(568, false, "人脸信息不能为空！"),
    ADMIN_FACE_LOGIN_ERROR(569, false, "人脸识别失败，请重试！"),
    ADMIN_DELETE_ERROR(5691, false, "删除管理员失败！"),
    CATEGORY_EXIST_ERROR(570, false, "文章分类已存在，请换一个分类名！"),
    ARTICLE_COVER_NOT_EXIST_ERROR(580, false, "文章封面不存在，请选择一个！"),
    ARTICLE_CATEGORY_NOT_EXIST_ERROR(581, false, "请选择正确的文章领域！"),
    ARTICLE_CREATE_ERROR(582, false, "创建文章失败，请重试或联系管理员！"),
    ARTICLE_QUERY_PARAMS_ERROR(583, false, "文章列表查询参数错误！"),
    ARTICLE_DELETE_ERROR(584, false, "文章删除失败！"),
    ARTICLE_WITHDRAW_ERROR(585, false, "文章撤回失败！"),
    ARTICLE_REVIEW_ERROR(585, false, "文章审核出错！"),
    ARTICLE_ALREADY_READ_ERROR(586, false, "文章重复阅读！"),
    COMPANY_INFO_UPDATED_ERROR(5151, false, "企业信息修改失败！"),
    COMPANY_INFO_UPDATED_NO_AUTH_ERROR(5151, false, "当前用户无权修改企业信息！"),
    COMPANY_IS_NOT_VIP_ERROR(5152, false, "企业非VIP或VIP特权已过期，请至企业后台充值续费！"),
    FACE_VERIFY_TYPE_ERROR(600, false, "人脸比对验证类型不正确！"),
    FACE_VERIFY_LOGIN_ERROR(601, false, "人脸登录失败！"),
    SYSTEM_ERROR(555, false, "系统繁忙，请稍后再试！"),
    SYSTEM_OPERATION_ERROR(556, false, "操作失败，请重试或联系管理员"),
    SYSTEM_RESPONSE_NO_INFO(557, false, ""),
    SYSTEM_ERROR_GLOBAL(558, false, "全局降级：系统繁忙，请稍后再试！"),
    SYSTEM_ERROR_FEIGN(559, false, "客户端Feign降级：系统繁忙，请稍后再试！"),
    SYSTEM_ERROR_ZUUL(560, false, "请求系统过于繁忙，请稍后再试！"),
    SYSTEM_PARAMS_SETTINGS_ERROR(5611, false, "参数设置不规范！"),
    ZOOKEEPER_BAD_VERSION_ERROR(5612, false, "数据过时，请刷新页面重试！"),
    SYSTEM_ERROR_BLACK_IP(5621, false, "请求过于频繁，请稍后重试！"),
    SYSTEM_SMS_FALLBACK_ERROR(5587, false, "短信业务繁忙，请稍后再试！"),
    SYS_DATA_ERROR(5588, false, "系统参数为空，请检查系统参数表sys_params！"),
    SYSTEM_ERROR_NOT_BLANK(5599, false, "系统错误，参数不能为空！"),
    DATA_DICT_EXIST_ERROR(5631, false, "数据字典已存在，不可重复添加或修改！"),
    DATA_DICT_DELETE_ERROR(5632, false, "删除数据字典失败！"),
    REPORT_RECORD_EXIST_ERROR(5721, false, "请不要重复举报噢~！"),
    RESUME_MAX_LIMIT_ERROR(5711, false, "本日简历刷新次数已达上限！"),
    JWT_SIGNATURE_ERROR(5555, false, "用户校验失败，请重新登录！"),
    JWT_EXPIRE_ERROR(5556, false, "登录有效期已过，请重新登录！"),
    SENTINEL_BLOCK_FLOW_LIMIT_ERROR(5801, false, "系统访问繁忙，请稍后再试！"),
    PAYMENT_USER_INFO_ERROR(5901, false, "用户id或密码不正确！"),
    PAYMENT_ACCOUT_EXPIRE_ERROR(5902, false, "该账户授权访问日期已失效！"),
    PAYMENT_HEADERS_ERROR(5903, false, "请在header中携带支付中心所需的用户id以及密码！"),
    PAYMENT_ORDER_CREATE_ERROR(5904, false, "支付中心订单创建失败，请联系管理员！"),
    ADMIN_NOT_EXIST(5101, false, "管理员不存在！");

    private Integer status;

    private Boolean success;

    private String msg;

    ResponseStatusEnum(Integer status, Boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
    }

    public Integer status() {
        return status;
    }

    public Boolean success() {
        return success;
    }

    public String msg() {
        return msg;
    }
}
