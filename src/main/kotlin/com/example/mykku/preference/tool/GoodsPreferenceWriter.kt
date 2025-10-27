package com.example.mykku.preference.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.GoodsType
import com.example.mykku.preference.domain.MemberGoodsPreference
import com.example.mykku.preference.repository.MemberGoodsPreferenceRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GoodsPreferenceWriter(
    private val memberGoodsPreferenceRepository: MemberGoodsPreferenceRepository
) {
    @Transactional
    fun replacePreferences(member: Member, goodsTypes: List<GoodsType>) {
        memberGoodsPreferenceRepository.deleteByMember(member)

        val preferences = goodsTypes.map { goodsType ->
            MemberGoodsPreference(
                member = member,
                goodsType = goodsType
            )
        }

        memberGoodsPreferenceRepository.saveAll(preferences)
    }
}
