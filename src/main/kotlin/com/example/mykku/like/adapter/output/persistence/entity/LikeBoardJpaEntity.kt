package com.example.mykku.like.adapter.output.persistence.entity

import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.like.domain.entity.LikeBoardEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
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
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    val board: BoardJpaEntity
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
            member: MemberJpaEntity,
            board: BoardJpaEntity
        ): LikeBoardJpaEntity {
            return LikeBoardJpaEntity(
                id = domain.id?.value,
                member = member,
                board = board
            )
        }
    }
}
