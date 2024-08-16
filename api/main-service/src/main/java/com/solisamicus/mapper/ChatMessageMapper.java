package com.solisamicus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solisamicus.pojo.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

}
