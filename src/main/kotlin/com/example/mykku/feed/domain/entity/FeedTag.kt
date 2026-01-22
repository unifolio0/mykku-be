package com.example.mykku.feed.domain.entity

import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.domain.vo.FeedTagId
import com.example.mykku.feed.exception.FeedException
import java.time.LocalDateTime

class FeedTag private constructor(
    val id: FeedTagId?,
    val title: String,
    val feedId: FeedId,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        const val TITLE_MAX_LENGTH = 20
        val VALID_PATTERN = Regex("^[가-힣a-zA-Z0-9]+$")

        fun create(
            title: String,
            feedId: FeedId
        ): FeedTag {
            validateTitle(title)
            val now = LocalDateTime.now()
            return FeedTag(
                id = null,
                title = title,
                feedId = feedId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            title: String,
            feedId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FeedTag {
            return FeedTag(
                id = FeedTagId.of(id),
                title = title,
                feedId = FeedId.of(feedId),
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        private fun validateTitle(title: String) {
            if (title.length > TITLE_MAX_LENGTH) {
                throw FeedException.tagTitleTooLong()
            }
            if (!VALID_PATTERN.matches(title)) {
                throw FeedException.tagInvalidFormat()
            }
        }
    }
}
