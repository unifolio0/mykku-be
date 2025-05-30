package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.BasicEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BasicEventRepository : JpaRepository<BasicEvent, Long> {
    @Query(
        """
            SELECT be
            FROM BasicEvent be
            WHERE be.expiredAt > :dateTime
        """
    )
    fun getByEventPreviews(dateTime: LocalDateTime): List<BasicEvent>
}
