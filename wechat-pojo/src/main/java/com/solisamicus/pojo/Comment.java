package com.solisamicus.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String belongUserId;

    private String fatherId;

    private String friendCircleId;

    private String commentUserId;

    private String commentContent;

    private LocalDateTime createdTime;
}
