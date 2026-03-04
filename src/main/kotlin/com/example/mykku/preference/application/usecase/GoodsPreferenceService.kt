package com.example.mykku.preference.application.usecase

import com.example.mykku.preference.application.dto.GoodsPreferenceResult
import com.example.mykku.preference.application.dto.UpdateGoodsPreferenceCommand
import com.example.mykku.preference.application.port.input.GetGoodsPreferenceUseCase
import com.example.mykku.preference.application.port.input.UpdateGoodsPreferenceUseCase
import com.example.mykku.preference.application.port.output.GoodsPreferenceRepository
import com.example.mykku.preference.domain.entity.MemberGoodsPreference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GoodsPreferenceService(
    private val goodsPreferenceRepository: GoodsPreferenceRepository
) : GetGoodsPreferenceUseCase, UpdateGoodsPreferenceUseCase {

    @Transactional(readOnly = true)
    override fun getGoodsPreferences(memberId: Long): GoodsPreferenceResult {
        val preferences = goodsPreferenceRepository.findByMemberId(memberId)
        return GoodsPreferenceResult(
            goodsTypes = preferences.map { it.goodsType }
        )
    }

    @Transactional
    override fun updateGoodsPreferences(command: UpdateGoodsPreferenceCommand) {
        goodsPreferenceRepository.deleteByMemberId(command.memberId)

        val preferences = command.goodsTypes.map { goodsType ->
            MemberGoodsPreference.create(
                memberId = command.memberId,
                goodsType = goodsType
            )
        }

        goodsPreferenceRepository.saveAll(preferences)
    }
}
