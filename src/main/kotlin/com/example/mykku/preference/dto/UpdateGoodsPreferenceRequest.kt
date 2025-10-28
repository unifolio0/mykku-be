package com.example.mykku.preference.dto

import com.example.mykku.preference.domain.GoodsType

data class UpdateGoodsPreferenceRequest(
    val goodsTypes: List<GoodsType>
)
