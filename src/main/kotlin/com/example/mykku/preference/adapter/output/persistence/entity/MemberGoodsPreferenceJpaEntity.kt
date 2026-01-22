package com.example.mykku.preference.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.preference.domain.entity.MemberGoodsPreference
import com.example.mykku.preference.domain.vo.GoodsType
import com.example.mykku.preference.domain.vo.PreferenceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member_goods_preference")
class MemberGoodsPreferenceJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberJpaEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "goods_type", nullable = false, length = 50)
    val goodsType: GoodsType
) : BaseEntity() {

    fun toDomain(): MemberGoodsPreference {
        return MemberGoodsPreference.reconstitute(
            id = PreferenceId.of(id),
            memberId = member.id,
            goodsType = goodsType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: MemberGoodsPreference, member: MemberJpaEntity): MemberGoodsPreferenceJpaEntity {
            return MemberGoodsPreferenceJpaEntity(
                id = domain.id.value,
                member = member,
                goodsType = domain.goodsType
            )
        }
    }
}
