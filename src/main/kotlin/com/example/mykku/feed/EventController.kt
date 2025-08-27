package com.example.mykku.feed

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.dto.CreateEventRequest
import com.example.mykku.feed.dto.CreateEventResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val eventService: EventService
) {

    @PostMapping
    fun createEvent(
        @RequestBody request: CreateEventRequest
    ): ResponseEntity<ApiResponse<CreateEventResponse>> {
        val response = eventService.createEvent(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    message = "이벤트가 성공적으로 생성되었습니다.",
                    data = response
                )
            )
    }
}
