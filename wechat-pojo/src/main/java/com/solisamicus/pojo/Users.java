package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String wechatNum;

    private String wechatNumImg;

    private String mobile;

    private String nickname;

    private String realName;

    private Integer sex;

    private String face;

    private String email;

    private LocalDate birthday;

    private String country;

    private String province;

    private String city;

    private String district;

    private String chatBg;

    private String friendCircleBg;

    private String signature;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
