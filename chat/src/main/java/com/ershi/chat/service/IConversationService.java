package com.ershi.chat.service;

import com.ershi.chat.domain.vo.ConversationResp;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.CursorPageBaseResp;

/**
 * 会话服务
 *
 * @author Ershi-Gu.
 * @since 2025-09-25
 */
public interface IConversationService {

    /**
     * 滑动查询会话列表
     *
     * @param cursorPageBaseReq
     * @return {@link CursorPageBaseResp }<{@link ConversationResp }>
     */
    CursorPageBaseResp<ConversationResp> getConversationPage(CursorPageBaseReq cursorPageBaseReq);
}
