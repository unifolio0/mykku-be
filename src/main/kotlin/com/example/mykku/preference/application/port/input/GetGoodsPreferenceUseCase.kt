package com.example.mykku.preference.application.port.input

import com.example.mykku.preference.application.dto.GoodsPreferenceResult

interface GetGoodsPreferenceUseCase {
    fun getGoodsPreferences(memberId: String): GoodsPreferenceResult
}
