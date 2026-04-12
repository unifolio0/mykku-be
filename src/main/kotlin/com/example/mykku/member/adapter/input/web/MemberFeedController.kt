package com.example.mykku.member.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.feed.adapter.input.web.dto.PagedFeedsResponse
import com.example.mykku.feed.application.dto.GetMyFeedsQuery
import com.example.mykku.feed.application.port.input.GetMyFeedsUseCase
import com.example.mykku.member.domain.entity.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members/me/feeds")
class MemberFeedController(
    private val getMyFeedsUseCase: GetMyFeedsUseCase
) {

    @GetMapping
    fun getMyFeeds(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedFeedsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val query = GetMyFeedsQuery(memberId = member.id.value, pageable = pageable)
        val result = getMyFeedsUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 피드 목록을 성공적으로 조회했습니다.",
                data = PagedFeedsResponse.from(result)
            )
        )
    }
}
