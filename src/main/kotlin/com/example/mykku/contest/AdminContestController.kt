package com.example.mykku.contest

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.contest.dto.SetContestWinnersRequest
import com.example.mykku.contest.dto.SetContestWinnersResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/contests")
class AdminContestController(
    private val contestWinnerService: ContestWinnerService
) {
    @PostMapping("/{contestId}/winners")
    fun setWinners(
        @PathVariable contestId: Long,
        @RequestBody @Valid request: SetContestWinnersRequest
    ): ResponseEntity<ApiResponse<SetContestWinnersResponse>> {
        val response = contestWinnerService.setWinners(contestId, request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상자가 성공적으로 선정되었습니다.",
                data = response
            )
        )
    }
}
