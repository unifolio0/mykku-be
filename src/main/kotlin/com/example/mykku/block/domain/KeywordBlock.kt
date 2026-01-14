package com.example.mykku.block.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
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
class KeywordBlock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @Column(name = "keyword", length = 50)
    val keyword: String
) : BaseEntity() {

    companion object {
        const val KEYWORD_MAX_LENGTH = 50
        const val MAX_KEYWORD_COUNT = 100
    }
}
