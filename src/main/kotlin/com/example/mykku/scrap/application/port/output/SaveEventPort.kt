package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.application.dto.SaveEventResult
import com.example.mykku.scrap.domain.entity.SaveEventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveEventPort {
    fun save(saveEvent: SaveEventEntity): SaveEventEntity
    fun existsByMemberIdAndEventId(memberId: String, eventId: Long): Boolean
    fun findByMemberId(memberId: String, pageable: Pageable): Page<SaveEventResult>
    fun deleteByMemberIdAndEventId(memberId: String, eventId: Long)
    fun findByMemberIdAndEventIdIn(memberId: String, eventIds: List<Long>): List<SaveEventEntity>
}
