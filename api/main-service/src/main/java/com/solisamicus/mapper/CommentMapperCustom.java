package com.solisamicus.mapper;

import com.solisamicus.pojo.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapperCustom{
    List<CommentVO> queryFriendCircleCommentList(@Param("paramMap") Map<String, Object> map);
}
