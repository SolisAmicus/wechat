package com.solisamicus.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.pojo.vo.FriendCircleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface FriendCircleMapperCustom {
    Page<FriendCircleVO> queryFriendCircleList(@Param("page") Page<FriendCircleVO> page,
                                               @Param("paramMap") Map<String, Object> map);
}
