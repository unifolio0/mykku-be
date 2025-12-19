package com.example.mykku.scrap.domain.model

import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class FolderDomain private constructor(
    val id: FolderId?,
    val memberId: MemberId,
    private var _name: String,
    private var _description: String?,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val name: String get() = _name
    val description: String? get() = _description
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            memberId: MemberId,
            name: String,
            description: String?
        ): FolderDomain {
            val now = Instant.now()
            return FolderDomain(
                id = null,
                memberId = memberId,
                _name = name,
                _description = description,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: FolderId,
            memberId: MemberId,
            name: String,
            description: String?,
            createdAt: Instant,
            updatedAt: Instant
        ): FolderDomain {
            return FolderDomain(
                id = id,
                memberId = memberId,
                _name = name,
                _description = description,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun updateInfo(name: String, description: String?) {
        _name = name
        _description = description
        _updatedAt = Instant.now()
    }
}
