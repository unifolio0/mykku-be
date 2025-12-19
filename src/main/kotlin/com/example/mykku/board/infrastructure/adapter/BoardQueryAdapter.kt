package com.example.mykku.board.infrastructure.adapter

import com.example.mykku.board.application.port.out.BoardQueryPort
import com.example.mykku.board.application.port.out.BoardSummary
import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.board.repository.BoardRepository
import org.springframework.stereotype.Component

@Component
class BoardQueryAdapter(
    private val boardRepository: BoardRepository
) : BoardQueryPort {

    override fun findSummaryById(id: BoardId): BoardSummary? {
        return boardRepository.findById(id.value)
            .map { board ->
                BoardSummary(
                    id = board.id!!,
                    title = board.title,
                    logo = board.logo
                )
            }
            .orElse(null)
    }

    override fun existsById(id: BoardId): Boolean {
        return boardRepository.existsById(id.value)
    }
}
