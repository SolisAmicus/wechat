package com.solisamicus.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentBO {
    private String belongUserId;
    private String fatherId;
    private String friendCircleId;
    private String commentUserId;
    private String commentContent;
}

