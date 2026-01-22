package com.example.mykku.event.application.port.output

import com.example.mykku.event.domain.entity.Event
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventSortType
import com.example.mykku.event.domain.vo.EventStatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface EventRepository {
    fun save(event: Event): Event
    fun findById(id: EventId): Event?
    fun findByExpiredAtAfter(dateTime: LocalDateTime): List<Event>
    fun findWithPagination(
        status: EventStatusType,
        sortType: EventSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Event>
}
