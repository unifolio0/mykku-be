package com.example.mykku.contest.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.contest.application.dto.GetMyAwardFeedsQuery
import com.example.mykku.contest.application.dto.GetMyAwardsPreviewQuery
import com.example.mykku.contest.application.dto.GetMyAwardsQuery
import com.example.mykku.contest.application.dto.GetMyWinnerStatusQuery
import com.example.mykku.contest.application.port.input.GetContestWinnerDetailUseCase
import com.example.mykku.contest.application.port.input.GetContestWinnersListUseCase
import com.example.mykku.contest.application.port.input.GetMyAwardContestsUseCase
import com.example.mykku.contest.application.port.input.GetMyAwardFeedsUseCase
import com.example.mykku.contest.application.port.input.GetMyAwardsPreviewUseCase
import com.example.mykku.contest.application.port.input.GetMyWinnerStatusUseCase
import com.example.mykku.contest.application.port.input.UpdateAcceptanceSpeechUseCase
import com.example.mykku.feed.adapter.input.web.dto.PagedFeedsResponse
import com.example.mykku.member.domain.entity.Member
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/contests")
class ContestWinnerController(
    private val getContestWinnersListUseCase: GetContestWinnersListUseCase,
    private val getContestWinnerDetailUseCase: GetContestWinnerDetailUseCase,
    private val updateAcceptanceSpeechUseCase: UpdateAcceptanceSpeechUseCase,
    private val getMyWinnerStatusUseCase: GetMyWinnerStatusUseCase,
    private val getMyAwardContestsUseCase: GetMyAwardContestsUseCase,
    private val getMyAwardFeedsUseCase: GetMyAwardFeedsUseCase,
    private val getMyAwardsPreviewUseCase: GetMyAwardsPreviewUseCase
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

    @GetMapping("/{contestId}/my-winner-status")
    fun getMyWinnerStatus(
        @PathVariable contestId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<MyWinnerStatusResponse>> {
        val query = GetMyWinnerStatusQuery(
            contestId = contestId,
            memberId = member.id.value
        )
        val result = getMyWinnerStatusUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상 여부를 성공적으로 조회했습니다.",
                data = MyWinnerStatusResponse.from(result)
            )
        )
    }

    @GetMapping("/my-awards")
    fun getMyAwardContests(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedMyAwardsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val query = GetMyAwardsQuery(memberId = member.id.value, pageable = pageable)
        val result = getMyAwardContestsUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 수상 콘테스트 목록을 성공적으로 조회했습니다.",
                data = PagedMyAwardsResponse.from(result)
            )
        )
    }

    @GetMapping("/my-awards/feeds")
    fun getMyAwardFeeds(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedFeedsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val query = GetMyAwardFeedsQuery(memberId = member.id.value, pageable = pageable)
        val result = getMyAwardFeedsUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 수상 피드 목록을 성공적으로 조회했습니다.",
                data = PagedFeedsResponse.from(result)
            )
        )
    }

    @GetMapping("/my-awards/preview")
    fun getMyAwardsPreview(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<MyAwardPreviewResponse>>> {
        val query = GetMyAwardsPreviewQuery(memberId = member.id.value)
        val result = getMyAwardsPreviewUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 수상 미리보기를 성공적으로 조회했습니다.",
                data = result.map { MyAwardPreviewResponse.from(it) }
            )
        )
    }

    @PatchMapping("/winners/{winnerId}/acceptance-speech")
    fun updateAcceptanceSpeech(
        @PathVariable winnerId: Long,
        @CurrentMember member: Member,
        @RequestBody @Valid request: UpdateAcceptanceSpeechRequest
    ): ResponseEntity<ApiResponse<UpdateAcceptanceSpeechResponse>> {
        val result = updateAcceptanceSpeechUseCase.execute(request.toCommand(winnerId, member.id.value))
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상 소감이 성공적으로 등록되었습니다.",
                data = UpdateAcceptanceSpeechResponse.from(result)
            )
        )
    }
}
