package com.example.mykku.preference.application.port.output

import com.example.mykku.preference.domain.entity.MemberGoodsPreference
import com.example.mykku.preference.domain.vo.GoodsType

interface GoodsPreferenceRepository {
    fun saveAll(preferences: List<MemberGoodsPreference>): List<MemberGoodsPreference>
    fun findByMemberId(memberId: String): List<MemberGoodsPreference>
    fun deleteByMemberId(memberId: String)
    fun existsByMemberIdAndGoodsType(memberId: String, goodsType: GoodsType): Boolean
}
