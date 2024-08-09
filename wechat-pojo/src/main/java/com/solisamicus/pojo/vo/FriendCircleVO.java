package com.solisamicus.pojo.vo;

import com.solisamicus.pojo.FriendCircleLiked;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendCircleVO{
    private String friendCircleId;
    private String userId;
    private String userNickname;
    private String userFace;
    private String words;
    private String images;
    private LocalDateTime publishTime;
    private List<FriendCircleLiked> likedFriends = new ArrayList<>();
    private Boolean doILike = false;
    private List<CommentVO> commentList = new ArrayList<>();
}