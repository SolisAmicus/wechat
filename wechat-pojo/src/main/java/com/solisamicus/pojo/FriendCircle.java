package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("friend_circle")
public class FriendCircle implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String userId;

    private String words;

    private String images;

    private String video;

    private LocalDateTime publishTime;
}
