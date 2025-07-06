package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Event
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
}
