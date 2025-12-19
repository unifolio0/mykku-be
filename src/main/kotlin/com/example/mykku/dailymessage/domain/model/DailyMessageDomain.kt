package com.example.mykku.dailymessage.domain.model

import java.time.Instant
import java.time.LocalDate

class DailyMessageDomain private constructor(
    val id: DailyMessageId?,
    val title: String,
    val content: DailyMessageContent,
    val date: LocalDate,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            title: String,
            content: DailyMessageContent,
            date: LocalDate
        ): DailyMessageDomain {
            val now = Instant.now()
            return DailyMessageDomain(
                id = null,
                title = title,
                content = content,
                date = date,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: DailyMessageId,
            title: String,
            content: DailyMessageContent,
            date: LocalDate,
            createdAt: Instant,
            updatedAt: Instant
        ): DailyMessageDomain {
            return DailyMessageDomain(
                id = id,
                title = title,
                content = content,
                date = date,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }
}
