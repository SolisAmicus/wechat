package com.solisamicus.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentVO {
    private String commentId;
    private String belongUserId;
    private String friendCircleId;
    private String fatherId;
    private String commentUserId;
    private String commentContent;
    private LocalDateTime createdTime;
    private String commentUserNickname;
    private String commentUserFace;
    private String replyedUserNickname;
}
