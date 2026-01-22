package com.example.mykku.preference.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.preference.adapter.output.persistence.entity.MemberGoodsPreferenceJpaEntity
import com.example.mykku.preference.adapter.output.persistence.repository.MemberGoodsPreferenceJpaRepository
import com.example.mykku.preference.application.port.output.GoodsPreferenceRepository
import com.example.mykku.preference.domain.entity.MemberGoodsPreference
import com.example.mykku.preference.domain.vo.GoodsType
import org.springframework.stereotype.Component

@Component
class GoodsPreferenceRepositoryAdapter(
    private val jpaRepository: MemberGoodsPreferenceJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : GoodsPreferenceRepository {

    override fun saveAll(preferences: List<MemberGoodsPreference>): List<MemberGoodsPreference> {
        if (preferences.isEmpty()) return emptyList()

        val memberId = preferences.first().memberId
        val member = memberJpaRepository.findById(memberId).orElseThrow {
            IllegalArgumentException("Member not found: $memberId")
        }

        val entities = preferences.map { preference ->
            MemberGoodsPreferenceJpaEntity.fromDomain(preference, member)
        }

        return jpaRepository.saveAll(entities).map { it.toDomain() }
    }

    override fun findByMemberId(memberId: String): List<MemberGoodsPreference> {
        return jpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun deleteByMemberId(memberId: String) {
        jpaRepository.deleteByMemberId(memberId)
    }

    override fun existsByMemberIdAndGoodsType(memberId: String, goodsType: GoodsType): Boolean {
        return jpaRepository.existsByMemberIdAndGoodsType(memberId, goodsType)
    }
}
