package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.application.dto.SaveDailyMessageResult
import com.example.mykku.scrap.domain.entity.SaveDailyMessageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveDailyMessagePort {
    fun save(saveDailyMessage: SaveDailyMessageEntity): SaveDailyMessageEntity
    fun existsByMemberIdAndDailyMessageId(memberId: Long, dailyMessageId: Long): Boolean
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<SaveDailyMessageResult>
    fun deleteByMemberIdAndDailyMessageId(memberId: Long, dailyMessageId: Long)
}
