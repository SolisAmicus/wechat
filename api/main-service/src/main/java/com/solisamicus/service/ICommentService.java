package com.solisamicus.service;

import com.solisamicus.pojo.bo.CommentBO;
import com.solisamicus.pojo.vo.CommentVO;

import java.util.List;

public interface ICommentService {
    CommentVO create(CommentBO commentBO);

    List<CommentVO> query(String friendCircleId);

    void delete(String commentId, String friendCircleId, String commentUserId);
}
