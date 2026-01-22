package com.example.mykku.board.adapter.output.persistence

import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardJpaRepository : JpaRepository<BoardJpaEntity, Long>
