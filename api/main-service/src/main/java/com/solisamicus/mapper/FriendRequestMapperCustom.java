package com.solisamicus.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.pojo.vo.FriendRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface FriendRequestMapperCustom {
    Page<FriendRequestVO> queryNewFriendRequestList(@Param("page") Page<FriendRequestVO> page,
                                                   @Param("paramMap") Map<String, Object> map);
}
