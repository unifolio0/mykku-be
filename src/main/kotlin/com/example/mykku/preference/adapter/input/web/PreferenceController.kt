package com.example.mykku.preference.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.preference.application.dto.UpdateGenrePreferenceCommand
import com.example.mykku.preference.application.dto.UpdateGoodsPreferenceCommand
import com.example.mykku.preference.application.dto.UpdateMoodPreferenceCommand
import com.example.mykku.preference.application.port.input.GetGenrePreferenceUseCase
import com.example.mykku.preference.application.port.input.GetGoodsPreferenceUseCase
import com.example.mykku.preference.application.port.input.GetMoodPreferenceUseCase
import com.example.mykku.preference.application.port.input.UpdateGenrePreferenceUseCase
import com.example.mykku.preference.application.port.input.UpdateGoodsPreferenceUseCase
import com.example.mykku.preference.application.port.input.UpdateMoodPreferenceUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/preferences")
class PreferenceController(
    private val getGenrePreferenceUseCase: GetGenrePreferenceUseCase,
    private val updateGenrePreferenceUseCase: UpdateGenrePreferenceUseCase,
    private val getGoodsPreferenceUseCase: GetGoodsPreferenceUseCase,
    private val updateGoodsPreferenceUseCase: UpdateGoodsPreferenceUseCase,
    private val getMoodPreferenceUseCase: GetMoodPreferenceUseCase,
    private val updateMoodPreferenceUseCase: UpdateMoodPreferenceUseCase
) {

    @PostMapping("/genre")
    fun updateGenrePreferences(
        @RequestBody @Valid request: UpdateGenrePreferenceRequest,
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UpdateGenrePreferenceCommand(
            memberId = memberEntity.id,
            genreTypes = request.genreTypes
        )
        updateGenrePreferenceUseCase.updateGenrePreferences(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "장르 취향이 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/goods")
    fun updateGoodsPreferences(
        @RequestBody @Valid request: UpdateGoodsPreferenceRequest,
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UpdateGoodsPreferenceCommand(
            memberId = memberEntity.id,
            goodsTypes = request.goodsTypes
        )
        updateGoodsPreferenceUseCase.updateGoodsPreferences(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "굿즈 취향이 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/mood")
    fun updateMoodPreferences(
        @RequestBody @Valid request: UpdateMoodPreferenceRequest,
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UpdateMoodPreferenceCommand(
            memberId = memberEntity.id,
            moodTypes = request.moodTypes
        )
        updateMoodPreferenceUseCase.updateMoodPreferences(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "분위기 취향이 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @GetMapping("/genre")
    fun getGenrePreferences(
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<GenrePreferenceResponse>> {
        val result = getGenrePreferenceUseCase.getGenrePreferences(memberEntity.id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "장르 취향을 성공적으로 조회했습니다.",
                data = GenrePreferenceResponse(result.genreTypes)
            )
        )
    }

    @GetMapping("/goods")
    fun getGoodsPreferences(
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<GoodsPreferenceResponse>> {
        val result = getGoodsPreferenceUseCase.getGoodsPreferences(memberEntity.id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "굿즈 취향을 성공적으로 조회했습니다.",
                data = GoodsPreferenceResponse(result.goodsTypes)
            )
        )
    }

    @GetMapping("/mood")
    fun getMoodPreferences(
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<MoodPreferenceResponse>> {
        val result = getMoodPreferenceUseCase.getMoodPreferences(memberEntity.id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "분위기 취향을 성공적으로 조회했습니다.",
                data = MoodPreferenceResponse(result.moodTypes)
            )
        )
    }
}
