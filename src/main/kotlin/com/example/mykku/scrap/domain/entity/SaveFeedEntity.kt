package com.example.mykku.scrap.domain.entity

import com.example.mykku.scrap.domain.vo.SaveFeedId
import java.time.LocalDateTime

class SaveFeedEntity private constructor(
    val id: SaveFeedId?,
    val memberId: String,
    val feedId: Long,
    var folderId: Long,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            feedId: Long,
            folderId: Long
        ): SaveFeedEntity {
            val now = LocalDateTime.now()
            return SaveFeedEntity(
                id = null,
                memberId = memberId,
                feedId = feedId,
                folderId = folderId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            feedId: Long,
            folderId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): SaveFeedEntity {
            return SaveFeedEntity(
                id = SaveFeedId.of(id),
                memberId = memberId,
                feedId = feedId,
                folderId = folderId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun updateFolder(newFolderId: Long) {
        this.folderId = newFolderId
        this.updatedAt = LocalDateTime.now()
    }
}
