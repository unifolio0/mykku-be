package com.example.mykku.preference.domain.model

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.preference.domain.GoodsType
import java.time.Instant

class GoodsPreferenceDomain private constructor(
    val id: GoodsPreferenceId?,
    val memberId: MemberId,
    val goodsType: GoodsType,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            goodsType: GoodsType
        ): GoodsPreferenceDomain {
            return GoodsPreferenceDomain(
                id = null,
                memberId = memberId,
                goodsType = goodsType,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: GoodsPreferenceId,
            memberId: MemberId,
            goodsType: GoodsType,
            createdAt: Instant
        ): GoodsPreferenceDomain {
            return GoodsPreferenceDomain(
                id = id,
                memberId = memberId,
                goodsType = goodsType,
                createdAt = createdAt
            )
        }
    }
}
