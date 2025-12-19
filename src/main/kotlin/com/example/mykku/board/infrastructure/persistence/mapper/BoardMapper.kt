package com.example.mykku.board.infrastructure.persistence.mapper

import com.example.mykku.board.domain.Board
import com.example.mykku.board.domain.model.BoardDomain
import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.board.domain.model.BoardTitle
import org.springframework.stereotype.Component
import java.time.ZoneId

@Component
class BoardMapper {

    fun toDomain(entity: Board): BoardDomain {
        return BoardDomain.reconstitute(
            id = BoardId(entity.id!!),
            title = BoardTitle(entity.title),
            logo = entity.logo,
            createdAt = entity.createdAt.atZone(ZoneId.systemDefault()).toInstant(),
            updatedAt = entity.updatedAt.atZone(ZoneId.systemDefault()).toInstant()
        )
    }

    fun toEntity(domain: BoardDomain): Board {
        return Board(
            id = domain.id?.value,
            title = domain.title.value,
            logo = domain.logo
        )
    }
}
