package com.example.mykku.event.adapter.output.persistence

import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.domain.entity.Event
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventSortType
import com.example.mykku.event.domain.vo.EventStatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class EventRepositoryAdapter(
    private val eventJpaRepository: EventJpaRepository
) : EventRepository {

    override fun save(event: Event): Event {
        val entity = EventJpaEntity.fromDomain(event)
        return eventJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: EventId): Event? {
        return eventJpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByExpiredAtAfter(dateTime: LocalDateTime): List<Event> {
        return eventJpaRepository.findByExpiredAtAfter(dateTime).map { it.toDomain() }
    }

    override fun findWithPagination(
        status: EventStatusType,
        sortType: EventSortType,
        pageable: Pageable,
        currentTime: LocalDateTime
    ): Page<Event> {
        val page = when (status) {
            EventStatusType.ACTIVE -> findActiveEvents(sortType, currentTime, pageable)
            EventStatusType.EXPIRED -> eventJpaRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(currentTime, pageable)
            EventStatusType.ALL -> eventJpaRepository.findAllByOrderByCreatedAtDesc(pageable)
            else -> eventJpaRepository.findAllByOrderByCreatedAtDesc(pageable)
        }
        return page.map { it.toDomain() }
    }

    private fun findActiveEvents(
        sortType: EventSortType,
        currentTime: LocalDateTime,
        pageable: Pageable
    ): Page<EventJpaEntity> {
        return when (sortType) {
            EventSortType.LATEST -> eventJpaRepository.findByExpiredAtAfterOrderByCreatedAtDesc(currentTime, pageable)
            EventSortType.OLDEST -> eventJpaRepository.findByExpiredAtAfterOrderByCreatedAtAsc(currentTime, pageable)
            EventSortType.POPULAR -> eventJpaRepository.findActiveEventsByPopular(currentTime, pageable)
        }
    }
}
