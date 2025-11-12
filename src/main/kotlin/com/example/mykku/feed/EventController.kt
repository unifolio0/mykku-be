package com.example.mykku.feed

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.domain.EventSortType
import com.example.mykku.feed.domain.EventStatusType
import com.example.mykku.feed.dto.CreateEventRequest
import com.example.mykku.feed.dto.CreateEventResponse
import com.example.mykku.feed.dto.EventDetailResponse
import com.example.mykku.feed.dto.PagedEventsResponse
import com.example.mykku.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트가 성공적으로 생성되었습니다.",
                data = response
            )
        )
    }

    @GetMapping
    fun getEvents(
        @RequestParam(defaultValue = "ACTIVE") status: EventStatusType,
        @RequestParam(defaultValue = "LATEST") sortType: EventSortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<PagedEventsResponse>> {
        val response = eventService.getEvents(status, sortType, page, size, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @GetMapping("/{eventId}")
    fun getEventDetail(
        @PathVariable eventId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<EventDetailResponse>> {
        val response = eventService.getEventDetail(eventId, member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "이벤트 상세 정보를 성공적으로 조회했습니다.",
                data = response
            )
        )
    }
}
