package com.example.mykku.board.domain.entity

import com.example.mykku.board.domain.vo.BoardId
import java.time.LocalDateTime

class Board private constructor(
    val id: BoardId,
    val title: String,
    val logo: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            title: String,
            logo: String
        ): Board {
            val now = LocalDateTime.now()
            return Board(
                id = BoardId(0L),
                title = title,
                logo = logo,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: BoardId,
            title: String,
            logo: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Board = Board(
            id = id,
            title = title,
            logo = logo,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
