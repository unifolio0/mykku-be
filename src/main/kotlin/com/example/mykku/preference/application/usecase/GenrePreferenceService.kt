package com.example.mykku.preference.application.usecase

import com.example.mykku.preference.application.dto.GenrePreferenceResult
import com.example.mykku.preference.application.dto.UpdateGenrePreferenceCommand
import com.example.mykku.preference.application.port.input.GetGenrePreferenceUseCase
import com.example.mykku.preference.application.port.input.UpdateGenrePreferenceUseCase
import com.example.mykku.preference.application.port.output.GenrePreferenceRepository
import com.example.mykku.preference.domain.entity.MemberGenrePreference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GenrePreferenceService(
    private val genrePreferenceRepository: GenrePreferenceRepository
) : GetGenrePreferenceUseCase, UpdateGenrePreferenceUseCase {

    @Transactional(readOnly = true)
    override fun getGenrePreferences(memberId: Long): GenrePreferenceResult {
        val preferences = genrePreferenceRepository.findByMemberId(memberId)
        return GenrePreferenceResult(
            genreTypes = preferences.map { it.genreType }
        )
    }

    @Transactional
    override fun updateGenrePreferences(command: UpdateGenrePreferenceCommand) {
        genrePreferenceRepository.deleteByMemberId(command.memberId)

        val preferences = command.genreTypes.map { genreType ->
            MemberGenrePreference.create(
                memberId = command.memberId,
                genreType = genreType
            )
        }

        genrePreferenceRepository.saveAll(preferences)
    }
}
