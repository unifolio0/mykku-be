package com.example.mykku.preference.adapter.output.persistence.repository

import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.preference.adapter.output.persistence.entity.MemberGoodsPreferenceJpaEntity
import com.example.mykku.preference.domain.vo.GoodsType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberGoodsPreferenceJpaRepository : JpaRepository<MemberGoodsPreferenceJpaEntity, Long> {
    fun findByMember(member: MemberJpaEntity): List<MemberGoodsPreferenceJpaEntity>
    fun findByMemberId(memberId: String): List<MemberGoodsPreferenceJpaEntity>
    fun deleteByMember(member: MemberJpaEntity)
    fun deleteByMemberId(memberId: String)
    fun existsByMemberAndGoodsType(member: MemberJpaEntity, goodsType: GoodsType): Boolean
    fun existsByMemberIdAndGoodsType(memberId: String, goodsType: GoodsType): Boolean
}
