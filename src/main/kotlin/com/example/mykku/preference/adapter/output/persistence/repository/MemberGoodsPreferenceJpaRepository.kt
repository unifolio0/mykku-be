package com.example.mykku.preference.adapter.output.persistence.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.adapter.output.persistence.entity.MemberGoodsPreferenceJpaEntity
import com.example.mykku.preference.domain.vo.GoodsType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberGoodsPreferenceJpaRepository : JpaRepository<MemberGoodsPreferenceJpaEntity, Long> {
    fun findByMember(member: Member): List<MemberGoodsPreferenceJpaEntity>
    fun findByMemberId(memberId: String): List<MemberGoodsPreferenceJpaEntity>
    fun deleteByMember(member: Member)
    fun deleteByMemberId(memberId: String)
    fun existsByMemberAndGoodsType(member: Member, goodsType: GoodsType): Boolean
    fun existsByMemberIdAndGoodsType(memberId: String, goodsType: GoodsType): Boolean
}
