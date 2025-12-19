package com.example.mykku.board.infrastructure.persistence.adapter

import com.example.mykku.board.application.port.out.BoardRepositoryPort
import com.example.mykku.board.domain.Board
import com.example.mykku.board.domain.model.BoardDomain
import com.example.mykku.board.domain.model.BoardId
import com.example.mykku.board.infrastructure.persistence.mapper.BoardMapper
import com.example.mykku.board.repository.BoardRepository
import org.springframework.stereotype.Repository

@Repository
class BoardRepositoryAdapter(
    private val boardRepository: BoardRepository,
    private val boardMapper: BoardMapper
) : BoardRepositoryPort {

    override fun save(board: BoardDomain): BoardDomain {
        val entity = boardMapper.toEntity(board)
        val savedEntity = boardRepository.save(entity)
        return boardMapper.toDomain(savedEntity)
    }

    override fun findById(id: BoardId): BoardDomain? {
        return boardRepository.findById(id.value)
            .map { boardMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(): List<BoardDomain> {
        return boardRepository.findAll().map { boardMapper.toDomain(it) }
    }

    override fun existsById(id: BoardId): Boolean {
        return boardRepository.existsById(id.value)
    }

    override fun createBoard(title: String, logo: String): Board {
        val board = Board(title = title, logo = logo)
        return boardRepository.save(board)
    }

    override fun updateBoard(board: Board, title: String, logo: String): Board {
        board.title = title
        board.logo = logo
        return boardRepository.save(board)
    }
}
