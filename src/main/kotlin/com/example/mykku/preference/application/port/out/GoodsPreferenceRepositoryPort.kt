package com.example.mykku.preference.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.GoodsType

interface GoodsPreferenceRepositoryPort {
    fun replacePreferences(member: Member, goodsTypes: List<GoodsType>)
}
