package com.solisamicus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solisamicus.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
