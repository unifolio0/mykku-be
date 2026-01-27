package com.example.mykku.block.adapter.output.persistence.entity

import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.domain.vo.KeywordBlockId
import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import jakarta.persistence.Column
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
    name = "keyword_block",
    uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "keyword"])]
)
class KeywordBlockJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @Column(name = "keyword", length = 50)
    val keyword: String
) : BaseJpaEntity() {

    fun toDomain(): KeywordBlock {
        return KeywordBlock.reconstitute(
            id = KeywordBlockId.of(id!!),
            memberId = member.id,
            keyword = keyword,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(keywordBlock: KeywordBlock, member: MemberJpaEntity): KeywordBlockJpaEntity {
            return KeywordBlockJpaEntity(
                member = member,
                keyword = keywordBlock.keyword
            )
        }
    }
}
