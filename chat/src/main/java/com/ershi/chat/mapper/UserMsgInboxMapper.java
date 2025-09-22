package com.ershi.chat.mapper;

import com.ershi.chat.domain.UserMsgInboxEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收件箱表 映射层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Mapper
public interface UserMsgInboxMapper extends BaseMapper<UserMsgInboxEntity> {


    void refreshInBox(List<Long> memberUidList, Long roomId, Long lastMsgId, LocalDateTime createTime);
}
