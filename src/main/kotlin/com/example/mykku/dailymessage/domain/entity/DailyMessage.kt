package com.example.mykku.dailymessage.domain.entity

import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageException
import java.time.LocalDate
import java.time.LocalDateTime

class DailyMessage private constructor(
    val id: DailyMessageId,
    val title: String,
    val content: String,
    val date: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        const val CONTENT_MAX_LENGTH = 42

        fun create(
            title: String,
            content: String,
            date: LocalDate
        ): DailyMessage {
            validateContent(content)
            val now = LocalDateTime.now()
            return DailyMessage(
                id = DailyMessageId(0L),
                title = title,
                content = content,
                date = date,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: DailyMessageId,
            title: String,
            content: String,
            date: LocalDate,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): DailyMessage {
            return DailyMessage(
                id = id,
                title = title,
                content = content,
                date = date,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        private fun validateContent(content: String) {
            if (content.length > CONTENT_MAX_LENGTH) {
                throw DailyMessageException.dailyMessageContentTooLong()
            }
        }
    }

    fun update(title: String, content: String, date: LocalDate): DailyMessage {
        validateContent(content)
        return DailyMessage(
            id = this.id,
            title = title,
            content = content,
            date = date,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }
}
