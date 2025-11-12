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
    @Query(
        """
            SELECT e
            FROM Event e
            WHERE e.expiredAt > :dateTime
        """
    )
    fun getByEventPreviews(dateTime: LocalDateTime): List<Event>

    @Query(
        """
            SELECT e
            FROM Event e
            WHERE e.expiredAt > :dateTime
            ORDER BY e.createdAt DESC
        """
    )
    fun findActiveEventsByLatest(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    @Query(
        """
            SELECT e
            FROM Event e
            WHERE e.expiredAt > :dateTime
            ORDER BY e.createdAt ASC
        """
    )
    fun findActiveEventsByOldest(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    @Query(
        """
            SELECT e
            FROM Event e
            WHERE e.expiredAt > :dateTime
            ORDER BY e.scrapCount DESC, e.createdAt DESC
        """
    )
    fun findActiveEventsByPopular(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    @Query(
        """
            SELECT e
            FROM Event e
            WHERE e.expiredAt <= :dateTime
            ORDER BY e.createdAt DESC
        """
    )
    fun findExpiredEventsWithPagination(dateTime: LocalDateTime, pageable: Pageable): Page<Event>

    @Query(
        """
            SELECT e
            FROM Event e
            ORDER BY e.createdAt DESC
        """
    )
    fun findAllEventsWithPagination(pageable: Pageable): Page<Event>
}
