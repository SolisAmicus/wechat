package com.solisamicus.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendCircleBO {
    private String userId;

    private String words;

    private String images;

    private String video;

    private LocalDateTime publishTime;
}