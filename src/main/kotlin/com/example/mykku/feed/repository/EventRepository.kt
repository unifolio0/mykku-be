package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventRepository : JpaRepository<Event, Long> {
    fun findByExpiredAtAfter(dateTime: LocalDateTime): List<Event>

    fun findByExpiredAtAfterOrderByCreatedAtDesc(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    fun findByExpiredAtAfterOrderByCreatedAtAsc(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    @Query(
        """
            SELECT e
            FROM Event e
            WHERE e.expiredAt > :dateTime
            ORDER BY e.scrapCount DESC, e.createdAt DESC
        """
    )
    fun findActiveEventsByPopular(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    fun findByExpiredAtLessThanEqualOrderByCreatedAtDesc(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Event>
}
