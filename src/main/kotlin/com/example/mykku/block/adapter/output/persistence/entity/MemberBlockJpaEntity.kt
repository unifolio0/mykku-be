package com.example.mykku.block.adapter.output.persistence.entity

import com.example.mykku.block.domain.entity.MemberBlock
import com.example.mykku.block.domain.vo.MemberBlockId
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "member_block",
    uniqueConstraints = [UniqueConstraint(columnNames = ["blocker_id", "blocked_id"])]
)
class MemberBlockJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    val blocker: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    val blocked: MemberJpaEntity
) : BaseEntity() {

    fun toDomain(): MemberBlock {
        return MemberBlock.reconstitute(
            id = MemberBlockId.of(id!!),
            blockerId = blocker.id,
            blockedId = blocked.id,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(
            memberBlock: MemberBlock,
            blocker: MemberJpaEntity,
            blocked: MemberJpaEntity
        ): MemberBlockJpaEntity {
            return MemberBlockJpaEntity(
                blocker = blocker,
                blocked = blocked
            )
        }
    }
}
