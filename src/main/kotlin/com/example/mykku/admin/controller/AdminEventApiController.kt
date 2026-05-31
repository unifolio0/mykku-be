package com.example.mykku.admin.controller

import com.example.mykku.admin.dto.event.EventCreateRequest
import com.example.mykku.admin.service.AdminEventService
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.event.adapter.input.web.CreateEventResponse
import com.example.mykku.event.adapter.input.web.SetEventWinnersRequest
import com.example.mykku.event.adapter.input.web.SetEventWinnersResponse
import com.example.mykku.event.application.port.input.SetEventWinnersUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/api/v1/events")
class AdminEventApiController(
    private val setEventWinnersUseCase: SetEventWinnersUseCase,
    private val adminEventService: AdminEventService
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun create(
        @Valid @ModelAttribute request: EventCreateRequest
    ): ResponseEntity<ApiResponse<CreateEventResponse>> {
        val created = adminEventService.create(request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트가 성공적으로 생성되었습니다.",
                data = created
            )
        )
    }

    @PostMapping("/{eventId}/winners")
    fun setWinners(
        @PathVariable eventId: Long,
        @RequestBody @Valid request: SetEventWinnersRequest
    ): ResponseEntity<ApiResponse<SetEventWinnersResponse>> {
        val result = setEventWinnersUseCase.execute(request.toCommand(eventId))
        return ResponseEntity.ok(
            ApiResponse(
                message = "당첨자가 성공적으로 선정되었습니다.",
                data = SetEventWinnersResponse.from(result)
            )
        )
    }
}
