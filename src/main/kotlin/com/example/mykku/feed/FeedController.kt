package com.example.mykku.feed

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.dto.*
import com.example.mykku.member.domain.Member
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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
        val imageList = images ?: emptyList()

        // 이미지 개수 제한 검증
        if (imageList.size > CreateFeedRequest.MAX_IMAGE_COUNT) {
            throw MykkuException(ErrorCode.FEED_IMAGE_LIMIT_EXCEEDED)
        }

        val request = CreateFeedRequest(
            title = requestDto.title,
            content = requestDto.content,
            boardId = requestDto.boardId,
            images = imageList,
            tags = requestDto.tags
        )

        val response = feedService.createFeed(request, member)
        return ResponseEntity.ok(
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
                message = "피드 목록 불러오기에 성공했습니다.",
                data = feeds
            )
        )
    }

    @GetMapping("/feeds/{feedId}/comments")
    fun getComments(
        @PathVariable feedId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember member: Member?
    ): ResponseEntity<ApiResponse<FeedCommentsResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val comments = feedCommentService.getComments(feedId, member?.id, pageable)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 목록을 성공적으로 조회했습니다.",
                data = comments
            )
        )
    }
}
