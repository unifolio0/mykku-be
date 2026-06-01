package com.example.mykku.event.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.event.application.dto.GetMyAwardEventsQuery
import com.example.mykku.event.application.dto.GetMyEventWinnerStatusQuery
import com.example.mykku.event.application.port.input.GetEventWinnersUseCase
import com.example.mykku.event.application.port.input.GetMyAwardEventsUseCase
import com.example.mykku.event.application.port.input.GetMyEventWinnerStatusUseCase
import com.example.mykku.member.domain.entity.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/events")
class EventWinnerController(
    private val getEventWinnersUseCase: GetEventWinnersUseCase,
    private val getMyEventWinnerStatusUseCase: GetMyEventWinnerStatusUseCase,
    private val getMyAwardEventsUseCase: GetMyAwardEventsUseCase
) {

    @GetMapping("/my-awards")
    fun getMyAwardEvents(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedMyAwardEventsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val query = GetMyAwardEventsQuery(id = member.id.value, pageable = pageable)
        val result = getMyAwardEventsUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 당첨 이벤트 목록을 성공적으로 조회했습니다.",
                data = PagedMyAwardEventsResponse.from(result)
            )
        )
    }

    @GetMapping("/{eventId}/winners")
    fun getEventWinners(
        @PathVariable eventId: Long
    ): ResponseEntity<ApiResponse<EventWinnersResponse>> {
        val result = getEventWinnersUseCase.execute(eventId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트 당첨자 목록을 성공적으로 조회했습니다.",
                data = EventWinnersResponse.from(result)
            )
        )
    }

    @GetMapping("/{eventId}/my-winner-status")
    fun getMyWinnerStatus(
        @PathVariable eventId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<MyEventWinnerStatusResponse>> {
        val query = GetMyEventWinnerStatusQuery(eventId = eventId, id = member.id.value)
        val result = getMyEventWinnerStatusUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "당첨 여부를 성공적으로 조회했습니다.",
                data = MyEventWinnerStatusResponse.from(result)
            )
        )
    }
}
