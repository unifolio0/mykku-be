package com.example.mykku.member

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.contest.ContestService
import com.example.mykku.contest.dto.PagedContestsResponse
import com.example.mykku.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members/me/contests")
class MemberContestController(
    private val contestService: ContestService
) {

    @GetMapping
    fun getMyParticipatedContests(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedContestsResponse>> {
        val response = contestService.getMyParticipatedContests(member, page, size)
        return ResponseEntity.ok(
            ApiResponse(
                message = "참여한 콘테스트 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }
}
