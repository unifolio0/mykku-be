package com.example.mykku.board.repository

import com.example.mykku.board.domain.Board
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository : JpaRepository<Board, Long> {
    fun existsByTitle(title: String): Boolean
}
