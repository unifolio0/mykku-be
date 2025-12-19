package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.MemberGoodsPreference

interface GoodsPreferenceQueryPort {
    fun findByMember(member: Member): List<MemberGoodsPreference>
}
