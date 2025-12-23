package com.example.mykku.scrap.domain.model

import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class SaveFeedDomain private constructor(
    val id: SaveFeedId?,
    val memberId: MemberId,
    val feedId: FeedId,
    private var _folderId: FolderId,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val folderId: FolderId get() = _folderId
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            memberId: MemberId,
            feedId: FeedId,
            folderId: FolderId
        ): SaveFeedDomain {
            val now = Instant.now()
            return SaveFeedDomain(
                id = null,
                memberId = memberId,
                feedId = feedId,
                _folderId = folderId,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: SaveFeedId,
            memberId: MemberId,
            feedId: FeedId,
            folderId: FolderId,
            createdAt: Instant,
            updatedAt: Instant
        ): SaveFeedDomain {
            return SaveFeedDomain(
                id = id,
                memberId = memberId,
                feedId = feedId,
                _folderId = folderId,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun updateFolder(folderId: FolderId) {
        _folderId = folderId
        _updatedAt = Instant.now()
    }
}
