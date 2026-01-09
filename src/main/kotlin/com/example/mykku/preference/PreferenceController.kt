package com.example.mykku.preference

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.preference.dto.GenrePreferenceResponse
import com.example.mykku.preference.dto.GoodsPreferenceResponse
import com.example.mykku.preference.dto.MoodPreferenceResponse
import com.example.mykku.preference.dto.UpdateGenrePreferenceRequest
import com.example.mykku.preference.dto.UpdateGoodsPreferenceRequest
import com.example.mykku.preference.dto.UpdateMoodPreferenceRequest
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
    private val preferenceService: PreferenceService
) {

    @PostMapping("/genre")
    fun updateGenrePreferences(
        @RequestBody @Valid request: UpdateGenrePreferenceRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        preferenceService.updateGenrePreferences(member, request.genreTypes)
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
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        preferenceService.updateGoodsPreferences(member, request.goodsTypes)
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
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        preferenceService.updateMoodPreferences(member, request.moodTypes)
        return ResponseEntity.ok(
            ApiResponse(
                message = "분위기 취향이 성공적으로 저장되었습니다.",
                data = Unit
            )
        )
    }

    @GetMapping("/genre")
    fun getGenrePreferences(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<GenrePreferenceResponse>> {
        val genreTypes = preferenceService.getGenrePreferences(member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "장르 취향을 성공적으로 조회했습니다.",
                data = GenrePreferenceResponse(genreTypes)
            )
        )
    }

    @GetMapping("/goods")
    fun getGoodsPreferences(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<GoodsPreferenceResponse>> {
        val goodsTypes = preferenceService.getGoodsPreferences(member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "굿즈 취향을 성공적으로 조회했습니다.",
                data = GoodsPreferenceResponse(goodsTypes)
            )
        )
    }

    @GetMapping("/mood")
    fun getMoodPreferences(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<MoodPreferenceResponse>> {
        val moodTypes = preferenceService.getMoodPreferences(member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "분위기 취향을 성공적으로 조회했습니다.",
                data = MoodPreferenceResponse(moodTypes)
            )
        )
    }
}
