package com.example.mykku.feed.domain.entity

import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import java.time.LocalDateTime

class FeedComment private constructor(
    val id: FeedCommentId?,
    val content: String,
    val likeCount: Int,
    val feedId: FeedId,
    val parentCommentId: FeedCommentId?,
    val memberId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        const val CONTENT_MAX_LENGTH = 1000

        fun create(
            content: String,
            feedId: FeedId,
            memberId: String,
            parentCommentId: FeedCommentId? = null
        ): FeedComment {
            validateContent(content)
            val now = LocalDateTime.now()
            return FeedComment(
                id = null,
                content = content,
                likeCount = 0,
                feedId = feedId,
                parentCommentId = parentCommentId,
                memberId = memberId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            content: String,
            likeCount: Int,
            feedId: Long,
            parentCommentId: Long?,
            memberId: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FeedComment {
            return FeedComment(
                id = FeedCommentId.of(id),
                content = content,
                likeCount = likeCount,
                feedId = FeedId.of(feedId),
                parentCommentId = parentCommentId?.let { FeedCommentId.of(it) },
                memberId = memberId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        private fun validateContent(content: String) {
            if (content.length > CONTENT_MAX_LENGTH) {
                throw FeedException.feedCommentContentTooLong()
            }
        }
    }

    fun updateContent(newContent: String): FeedComment {
        validateContent(newContent)
        return FeedComment(
            id = this.id,
            content = newContent,
            likeCount = this.likeCount,
            feedId = this.feedId,
            parentCommentId = this.parentCommentId,
            memberId = this.memberId,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }

    fun isOwnedBy(memberId: String): Boolean = this.memberId == memberId
}
