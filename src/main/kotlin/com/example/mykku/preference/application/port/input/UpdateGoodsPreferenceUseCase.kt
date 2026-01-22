package com.example.mykku.preference.application.port.input

import com.example.mykku.preference.application.dto.UpdateGoodsPreferenceCommand

interface UpdateGoodsPreferenceUseCase {
    fun updateGoodsPreferences(command: UpdateGoodsPreferenceCommand)
}
