package com.example.mykku.preference.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MemberGoodsPreference
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberGoodsPreferenceRepository : JpaRepository<MemberGoodsPreference, Long> {
    fun findByMember(member: Member): List<MemberGoodsPreference>
    fun deleteByMember(member: Member)
    fun existsByMemberAndGoodsType(member: Member, goodsType: GoodsType): Boolean
}
