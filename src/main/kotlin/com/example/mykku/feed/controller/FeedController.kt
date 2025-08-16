package com.example.mykku.feed.controller

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.dto.CreateFeedRequest
import com.example.mykku.feed.dto.CreateFeedRequestDto
import com.example.mykku.feed.dto.CreateFeedResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.service.FeedService
import com.example.mykku.member.domain.Member
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1")
class FeedController(
    private val feedService: FeedService,
    private val objectMapper: ObjectMapper
) {
    @PostMapping("/feeds", consumes = ["multipart/form-data"])
    fun createFeed(
        @RequestPart("request") @Valid requestJson: String,
        @RequestPart("images", required = false) images: List<MultipartFile>?,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CreateFeedResponse>> {
        val requestDto = objectMapper.readValue(requestJson, CreateFeedRequestDto::class.java)
        
        val request = CreateFeedRequest(
            title = requestDto.title,
            content = requestDto.content,
            boardId = requestDto.boardId,
            images = images ?: emptyList(),
            tags = requestDto.tags
        )
        
        val response = feedService.createFeed(request, member)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                message = "피드가 성공적으로 작성되었습니다.",
                data = response
            )
        )
    }

    @GetMapping("/{memberId}/feeds")
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
