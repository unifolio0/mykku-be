package com.example.mykku.like.adapter.output.persistence.entity

import com.example.mykku.board.domain.Board
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.like.domain.entity.LikeBoardEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "like_board")
class LikeBoardJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    val board: Board
) : BaseEntity() {

    fun toDomain(): LikeBoardEntity {
        return LikeBoardEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            boardId = this.board.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: LikeBoardEntity,
            member: Member,
            board: Board
        ): LikeBoardJpaEntity {
            return LikeBoardJpaEntity(
                id = domain.id?.value,
                member = member,
                board = board
            )
        }
    }
}
