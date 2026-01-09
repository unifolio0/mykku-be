package com.example.mykku.contest

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.contest.domain.ContestSortType
import com.example.mykku.contest.domain.ContestStatusType
import com.example.mykku.contest.dto.ContestDetailResponse
import com.example.mykku.contest.dto.CreateContestRequest
import com.example.mykku.contest.dto.CreateContestResponse
import com.example.mykku.contest.dto.PagedContestsResponse
import com.example.mykku.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/contests")
class ContestController(
    private val contestService: ContestService
) {

    @PostMapping
    fun createContest(
        @RequestBody request: CreateContestRequest
    ): ResponseEntity<ApiResponse<CreateContestResponse>> {
        val response = contestService.createContest(request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "공모전이 성공적으로 생성되었습니다.",
                data = response
            )
        )
    }

    @GetMapping
    fun getContests(
        @RequestParam(defaultValue = "ACTIVE") status: ContestStatusType,
        @RequestParam(defaultValue = "LATEST") sortType: ContestSortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedContestsResponse>> {
        val response = contestService.getContests(status, sortType, page, size, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "공모전 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @GetMapping("/{contestId}")
    fun getContestDetail(
        @PathVariable contestId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<ContestDetailResponse>> {
        val response = contestService.getContestDetail(contestId, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "공모전 상세 정보를 성공적으로 조회했습니다.",
                data = response
            )
        )
    }
}
