package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {
    @Query(
        """
            SELECT t
            FROM Tag t
            WHERE t.isEvent = true
            AND t.expiredAt > :dateTime
        """
    )
    fun getByEventPreviews(dateTime: LocalDateTime): List<Tag>
}
