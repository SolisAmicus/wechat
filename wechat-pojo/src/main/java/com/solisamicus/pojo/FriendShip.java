package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("friend_ship")
public class FriendShip implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String myId;

    private String friendId;

    private String friendRemark;

    private String chatBg;

    private Integer isMsgIgnore;

    private Integer isBlack;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
