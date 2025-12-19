package com.example.mykku.preference.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.application.port.out.GoodsPreferenceQueryPort
import com.example.mykku.preference.domain.MemberGoodsPreference
import com.example.mykku.preference.repository.MemberGoodsPreferenceRepository
import org.springframework.stereotype.Component

@Component
class GoodsPreferenceQueryAdapter(
    private val memberGoodsPreferenceRepository: MemberGoodsPreferenceRepository
) : GoodsPreferenceQueryPort {
    override fun findByMember(member: Member): List<MemberGoodsPreference> {
        return memberGoodsPreferenceRepository.findByMember(member)
    }
}
