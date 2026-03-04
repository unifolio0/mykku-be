package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.application.dto.SaveEventResult
import com.example.mykku.scrap.domain.entity.SaveEventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveEventPort {
    fun save(saveEvent: SaveEventEntity): SaveEventEntity
    fun existsByMemberIdAndEventId(memberId: Long, eventId: Long): Boolean
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<SaveEventResult>
    fun deleteByMemberIdAndEventId(memberId: Long, eventId: Long)
    fun findByMemberIdAndEventIdIn(memberId: Long, eventIds: List<Long>): List<SaveEventEntity>
}
