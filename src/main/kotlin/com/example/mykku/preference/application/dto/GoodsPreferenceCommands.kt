package com.example.mykku.preference.application.dto

import com.example.mykku.preference.domain.vo.GoodsType

data class UpdateGoodsPreferenceCommand(
    val memberId: String,
    val goodsTypes: List<GoodsType>
)

data class GoodsPreferenceResult(
    val goodsTypes: List<GoodsType>
)
