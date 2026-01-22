package com.example.mykku.scrap.domain.entity

import com.example.mykku.scrap.domain.vo.FolderId
import java.time.LocalDateTime

class FolderEntity private constructor(
    val id: FolderId?,
    val memberId: String,
    var name: String,
    var description: String?,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            name: String,
            description: String?
        ): FolderEntity {
            val now = LocalDateTime.now()
            return FolderEntity(
                id = null,
                memberId = memberId,
                name = name,
                description = description,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            name: String,
            description: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FolderEntity {
            return FolderEntity(
                id = FolderId.of(id),
                memberId = memberId,
                name = name,
                description = description,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun updateInfo(newName: String, newDescription: String?) {
        this.name = newName
        this.description = newDescription
        this.updatedAt = LocalDateTime.now()
    }
}
