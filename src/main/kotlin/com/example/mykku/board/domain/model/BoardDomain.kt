package com.example.mykku.board.domain.model

import java.time.Instant

class BoardDomain private constructor(
    val id: BoardId?,
    val title: BoardTitle,
    val logo: String,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            title: BoardTitle,
            logo: String
        ): BoardDomain {
            val now = Instant.now()
            return BoardDomain(
                id = null,
                title = title,
                logo = logo,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: BoardId,
            title: BoardTitle,
            logo: String,
            createdAt: Instant,
            updatedAt: Instant
        ): BoardDomain {
            return BoardDomain(
                id = id,
                title = title,
                logo = logo,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }
}
