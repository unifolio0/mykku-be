package com.example.mykku.feed.controller

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.dto.CreateFeedRequest
import com.example.mykku.feed.dto.CreateFeedRequestDto
import com.example.mykku.feed.dto.CreateFeedResponse
import com.example.mykku.feed.dto.FeedCommentsResponse
import com.example.mykku.feed.dto.FeedsResponse
import com.example.mykku.feed.service.FeedCommentService
import com.example.mykku.feed.service.FeedService
import com.example.mykku.member.domain.Member
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@RestController
@RequestMapping("/api/v1")
class FeedController(
    private val feedService: FeedService,
    private val feedCommentService: FeedCommentService
) {
    @PostMapping("/feeds", consumes = ["multipart/form-data"])
    fun createFeed(
        @RequestPart("request") @Valid requestDto: CreateFeedRequestDto,
        @RequestPart("images", required = false) images: List<MultipartFile>?,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CreateFeedResponse>> {
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
    
    @GetMapping("/feeds/{feedId}/comments")
    fun getComments(
        @PathVariable feedId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<FeedCommentsResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val comments = feedCommentService.getComments(feedId, null, pageable)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 목록을 성공적으로 조회했습니다.",
                data = comments
            )
        )
    }
}
