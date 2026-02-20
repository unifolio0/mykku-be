package com.example.mykku.feed.domain.entity

import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import java.time.LocalDateTime

class Feed private constructor(
    val id: FeedId?,
    val title: String,
    val content: String,
    val likeCount: Int,
    val commentCount: Int,
    val boardId: Long,
    val memberId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        const val CONTENT_MAX_LENGTH = 1000
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7

        fun create(
            title: String,
            content: String,
            boardId: Long,
            memberId: String
        ): Feed {
            validateContent(content)
            val now = LocalDateTime.now()
            return Feed(
                id = null,
                title = title,
                content = content,
                likeCount = 0,
                commentCount = 0,
                boardId = boardId,
                memberId = memberId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            title: String,
            content: String,
            likeCount: Int,
            commentCount: Int,
            boardId: Long,
            memberId: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Feed {
            return Feed(
                id = FeedId.of(id),
                title = title,
                content = content,
                likeCount = likeCount,
                commentCount = commentCount,
                boardId = boardId,
                memberId = memberId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        private fun validateContent(content: String) {
            if (content.length > CONTENT_MAX_LENGTH) {
                throw FeedException.feedContentTooLong()
            }
        }
    }

    fun update(
        title: String? = null,
        content: String? = null,
        boardId: Long? = null
    ): Feed {
        val newContent = content ?: this.content
        if (content != null) {
            validateContent(newContent)
        }
        return Feed(
            id = this.id,
            title = title ?: this.title,
            content = newContent,
            likeCount = this.likeCount,
            commentCount = this.commentCount,
            boardId = boardId ?: this.boardId,
            memberId = this.memberId,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }

    fun isOwnedBy(memberId: String): Boolean = this.memberId == memberId
}
