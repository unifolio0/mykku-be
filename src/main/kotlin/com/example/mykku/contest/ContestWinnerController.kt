package com.example.mykku.contest

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.contest.dto.ContestWinnerDetailResponse
import com.example.mykku.contest.dto.ContestWinnersListResponse
import com.example.mykku.contest.dto.UpdateAcceptanceSpeechRequest
import com.example.mykku.contest.dto.UpdateAcceptanceSpeechResponse
import com.example.mykku.member.domain.Member
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/contests")
class ContestWinnerController(
    private val contestWinnerService: ContestWinnerService
) {
    @GetMapping("/winners")
    fun getContestsWithWinners(): ResponseEntity<ApiResponse<ContestWinnersListResponse>> {
        val response = contestWinnerService.getContestsWithWinners()
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상작 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @GetMapping("/{contestId}/winners")
    fun getContestWinnerDetail(
        @PathVariable contestId: Long
    ): ResponseEntity<ApiResponse<ContestWinnerDetailResponse>> {
        val response = contestWinnerService.getContestWinnerDetail(contestId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상작 상세 정보를 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @PatchMapping("/winners/{winnerId}/acceptance-speech")
    fun updateAcceptanceSpeech(
        @PathVariable winnerId: Long,
        @CurrentMember member: Member,
        @RequestBody @Valid request: UpdateAcceptanceSpeechRequest
    ): ResponseEntity<ApiResponse<UpdateAcceptanceSpeechResponse>> {
        val response = contestWinnerService.updateAcceptanceSpeech(winnerId, member, request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상 소감이 성공적으로 등록되었습니다.",
                data = response
            )
        )
    }
}
