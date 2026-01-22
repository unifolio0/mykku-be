package com.example.mykku.board.adapter.output.persistence.entity

import com.example.mykku.board.domain.entity.Board
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "board")
class BoardJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "logo")
    var logo: String
) : BaseEntity() {

    fun toDomain(): Board = Board.reconstitute(
        id = BoardId.of(id!!),
        title = title,
        logo = logo,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(board: Board): BoardJpaEntity = BoardJpaEntity(
            id = if (board.id.value == 0L) null else board.id.value,
            title = board.title,
            logo = board.logo
        )
    }
}
