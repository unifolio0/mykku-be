package com.example.mykku.preference.domain.entity

import com.example.mykku.preference.domain.vo.GoodsType
import com.example.mykku.preference.domain.vo.PreferenceId
import java.time.LocalDateTime

class MemberGoodsPreference private constructor(
    val id: PreferenceId,
    val memberId: Long,
    val goodsType: GoodsType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
            goodsType: GoodsType
        ): MemberGoodsPreference {
            val now = LocalDateTime.now()
            return MemberGoodsPreference(
                id = PreferenceId(0),
                memberId = memberId,
                goodsType = goodsType,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: PreferenceId,
            memberId: Long,
            goodsType: GoodsType,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberGoodsPreference {
            return MemberGoodsPreference(
                id = id,
                memberId = memberId,
                goodsType = goodsType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
