package com.example.mykku.contest.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.contest.application.port.input.GetContestWinnerDetailUseCase
import com.example.mykku.contest.application.port.input.GetContestWinnersListUseCase
import com.example.mykku.contest.application.port.input.UpdateAcceptanceSpeechUseCase
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
    private val getContestWinnersListUseCase: GetContestWinnersListUseCase,
    private val getContestWinnerDetailUseCase: GetContestWinnerDetailUseCase,
    private val updateAcceptanceSpeechUseCase: UpdateAcceptanceSpeechUseCase
) {

    @GetMapping("/winners")
    fun getContestsWithWinners(): ResponseEntity<ApiResponse<ContestWinnersListResponse>> {
        val result = getContestWinnersListUseCase.execute()
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상작 목록을 성공적으로 조회했습니다.",
                data = ContestWinnersListResponse.from(result)
            )
        )
    }

    @GetMapping("/{contestId}/winners")
    fun getContestWinnerDetail(
        @PathVariable contestId: Long
    ): ResponseEntity<ApiResponse<ContestWinnerDetailResponse>> {
        val result = getContestWinnerDetailUseCase.execute(contestId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상작 상세 정보를 성공적으로 조회했습니다.",
                data = ContestWinnerDetailResponse.from(result)
            )
        )
    }

    @PatchMapping("/winners/{winnerId}/acceptance-speech")
    fun updateAcceptanceSpeech(
        @PathVariable winnerId: Long,
        @CurrentMember member: Member,
        @RequestBody @Valid request: UpdateAcceptanceSpeechRequest
    ): ResponseEntity<ApiResponse<UpdateAcceptanceSpeechResponse>> {
        val result = updateAcceptanceSpeechUseCase.execute(request.toCommand(winnerId, member.id!!))
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상 소감이 성공적으로 등록되었습니다.",
                data = UpdateAcceptanceSpeechResponse.from(result)
            )
        )
    }
}
