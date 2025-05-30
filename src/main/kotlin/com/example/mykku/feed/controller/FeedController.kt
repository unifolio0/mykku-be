package com.example.mykku.feed.controller

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.service.FeedService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedController(
    private val feedService: FeedService,
) {
    @GetMapping("/api/{memberId}/feeds")
    fun getFeeds(@PathVariable memberId: String): ResponseEntity<ApiResponse<FeedsResponse>> {
        val feeds = feedService.getFeeds(memberId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "홈 피드 목록 불러오기에 성공했습니다.",
                data = feeds
            )
        )
    }
}
