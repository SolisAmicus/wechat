package com.solisamicus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.solisamicus.mapper.CommentMapper;
import com.solisamicus.mapper.CommentMapperCustom;
import com.solisamicus.mapper.UsersMapper;
import com.solisamicus.pojo.Comment;
import com.solisamicus.pojo.Users;
import com.solisamicus.pojo.bo.CommentBO;
import com.solisamicus.pojo.vo.CommentVO;
import com.solisamicus.service.ICommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentMapperCustom commentMapperCustom;

    @Autowired
    private UsersMapper usersMapper;

    @Transactional
    @Override
    public CommentVO create(CommentBO commentBO) {
        Comment pendingComment = new Comment();
        BeanUtils.copyProperties(commentBO, pendingComment);
        pendingComment.setCreatedTime(LocalDateTime.now());
        commentMapper.insert(pendingComment);
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(pendingComment, commentVO);
        Users user = usersMapper.selectById(commentBO.getCommentUserId());
        commentVO.setCommentUserNickname(user.getNickname());
        commentVO.setCommentUserFace(user.getFace());
        commentVO.setCommentId(pendingComment.getId());
        return commentVO;
    }

    @Override
    public List<CommentVO> query(String friendCircleId) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendCircleId", friendCircleId);
        return commentMapperCustom.queryFriendCircleCommentList(map);
    }

    @Override
    public void delete(String commentId, String friendCircleId, String commentUserId) {
        QueryWrapper<Comment> deleteWrapper = new QueryWrapper<Comment>().
                eq("id", commentId).
                eq("friend_circle_id", friendCircleId).
                eq("comment_user_id", commentUserId);
        commentMapper.delete(deleteWrapper);
    }
}
