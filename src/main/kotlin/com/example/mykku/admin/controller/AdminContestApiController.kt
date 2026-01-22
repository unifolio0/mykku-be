package com.example.mykku.admin.controller

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.contest.adapter.input.web.SetContestWinnersRequest
import com.example.mykku.contest.adapter.input.web.SetContestWinnersResponse
import com.example.mykku.contest.application.port.input.SetContestWinnersUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/api/v1/contests")
class AdminContestApiController(
    private val setContestWinnersUseCase: SetContestWinnersUseCase
) {

    @PostMapping("/{contestId}/winners")
    fun setWinners(
        @PathVariable contestId: Long,
        @RequestBody @Valid request: SetContestWinnersRequest
    ): ResponseEntity<ApiResponse<SetContestWinnersResponse>> {
        val result = setContestWinnersUseCase.execute(request.toCommand(contestId))
        return ResponseEntity.ok(
            ApiResponse(
                message = "수상자가 성공적으로 선정되었습니다.",
                data = SetContestWinnersResponse.from(result)
            )
        )
    }
}
