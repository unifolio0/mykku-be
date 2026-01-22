package com.example.mykku.member.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.event.EventService
import com.example.mykku.event.dto.PagedEventsResponse
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members/me/events")
class MemberEventController(
    private val eventService: EventService
) {

    @GetMapping
    fun getMyParticipatedEvents(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<PagedEventsResponse>> {
        val response = eventService.getMyParticipatedEvents(memberEntity, page, size)
        return ResponseEntity.ok(
            ApiResponse(
                message = "참여한 이벤트 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }
}
