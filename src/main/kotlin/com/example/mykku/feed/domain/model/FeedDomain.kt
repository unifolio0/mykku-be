package com.example.mykku.feed.domain.model

import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class FeedDomain private constructor(
    val id: FeedId?,
    val title: String,
    val content: FeedContent,
    val boardId: BoardId,
    val authorId: MemberId,
    private var _likeCount: Int,
    private var _commentCount: Int,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val likeCount: Int get() = _likeCount
    val commentCount: Int get() = _commentCount
    val updatedAt: Instant get() = _updatedAt

    companion object {
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7

        fun create(
            title: String,
            content: FeedContent,
            boardId: BoardId,
            authorId: MemberId
        ): FeedDomain {
            val now = Instant.now()
            return FeedDomain(
                id = null,
                title = title,
                content = content,
                boardId = boardId,
                authorId = authorId,
                _likeCount = 0,
                _commentCount = 0,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: FeedId,
            title: String,
            content: FeedContent,
            boardId: BoardId,
            authorId: MemberId,
            likeCount: Int,
            commentCount: Int,
            createdAt: Instant,
            updatedAt: Instant
        ): FeedDomain {
            return FeedDomain(
                id = id,
                title = title,
                content = content,
                boardId = boardId,
                authorId = authorId,
                _likeCount = likeCount,
                _commentCount = commentCount,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun incrementLikeCount() {
        _likeCount++
        _updatedAt = Instant.now()
    }

    fun decrementLikeCount() {
        if (_likeCount > 0) {
            _likeCount--
            _updatedAt = Instant.now()
        }
    }

    fun incrementCommentCount() {
        _commentCount++
        _updatedAt = Instant.now()
    }

    fun decrementCommentCount() {
        if (_commentCount > 0) {
            _commentCount--
            _updatedAt = Instant.now()
        }
    }
}
