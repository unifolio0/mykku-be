package com.example.mykku.preference.application.usecase

import com.example.mykku.preference.application.dto.MoodPreferenceResult
import com.example.mykku.preference.application.dto.UpdateMoodPreferenceCommand
import com.example.mykku.preference.application.port.input.GetMoodPreferenceUseCase
import com.example.mykku.preference.application.port.input.UpdateMoodPreferenceUseCase
import com.example.mykku.preference.application.port.output.MoodPreferenceRepository
import com.example.mykku.preference.domain.entity.MemberMoodPreference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MoodPreferenceService(
    private val moodPreferenceRepository: MoodPreferenceRepository
) : GetMoodPreferenceUseCase, UpdateMoodPreferenceUseCase {

    @Transactional(readOnly = true)
    override fun getMoodPreferences(memberId: Long): MoodPreferenceResult {
        val preferences = moodPreferenceRepository.findByMemberId(memberId)
        return MoodPreferenceResult(
            moodTypes = preferences.map { it.moodType }
        )
    }

    @Transactional
    override fun updateMoodPreferences(command: UpdateMoodPreferenceCommand) {
        moodPreferenceRepository.deleteByMemberId(command.memberId)

        val preferences = command.moodTypes.map { moodType ->
            MemberMoodPreference.create(
                memberId = command.memberId,
                moodType = moodType
            )
        }

        moodPreferenceRepository.saveAll(preferences)
    }
}
