package com.example.mykku.preference.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberGoodsPreference
import com.example.mykku.preference.repository.MemberGoodsPreferenceRepository
import org.springframework.stereotype.Component

@Component
class GoodsPreferenceReader(
    private val memberGoodsPreferenceRepository: MemberGoodsPreferenceRepository
) {
    fun findByMember(member: Member): List<MemberGoodsPreference> {
        return memberGoodsPreferenceRepository.findByMember(member)
    }
}
