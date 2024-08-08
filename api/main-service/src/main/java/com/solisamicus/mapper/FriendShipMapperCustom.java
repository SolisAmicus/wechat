package com.solisamicus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.pojo.FriendShip;
import com.solisamicus.pojo.vo.ContactsVO;
import com.solisamicus.pojo.vo.FriendRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FriendShipMapperCustom{
    List<ContactsVO> queryMyFriends(@Param("paramMap") Map<String, Object> map);
}
