package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("friend_request")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String myId;

    private String friendId;

    private String friendRemark;

    private String verifyMessage;

    private Integer verifyStatus;

    private LocalDateTime requestTime;
}
