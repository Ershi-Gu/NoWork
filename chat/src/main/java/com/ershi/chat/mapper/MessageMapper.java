package com.ershi.chat.mapper;

import com.ershi.chat.domain.dto.C10nUnreadCount;
import com.ershi.chat.domain.dto.UnReadMsgCountReq;
import com.ershi.chat.domain.message.MessageEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息表 映射层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {

    List<C10nUnreadCount> countUnreadMsg(@Param("unReadMsgCountReqs") List<UnReadMsgCountReq> unReadMsgCountReqs);
}
