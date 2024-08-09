package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("friend_circle_liked")
public class FriendCircleLiked implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String belongUserId;

    private String friendCircleId;

    private String likedUserId;

    private String likedUserName;

    private LocalDateTime createdTime;
}
