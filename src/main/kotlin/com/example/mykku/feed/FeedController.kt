package com.example.mykku.feed

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.dto.*
import com.example.mykku.member.domain.Member
import jakarta.validation.Valid
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
        val request = buildCreateFeedRequest(requestDto, images)
        val response = feedService.createFeed(request, member)

        return ResponseEntity.ok(
            ApiResponse(
                message = "피드가 성공적으로 작성되었습니다.",
                data = response
            )
        )
    }

    private fun buildCreateFeedRequest(
        requestDto: CreateFeedRequestDto,
        images: List<MultipartFile>?
    ): CreateFeedRequest {
        val imageList = images ?: emptyList()
        validateImageCount(imageList.size)

        return CreateFeedRequest(
            title = requestDto.title,
            content = requestDto.content,
            boardId = requestDto.boardId,
            images = imageList,
            tags = requestDto.tags
        )
    }

    private fun validateImageCount(imageCount: Int) {
        if (imageCount > CreateFeedRequest.MAX_IMAGE_COUNT) {
            throw FeedException.feedImageLimitExceeded()
        }
    }

    @GetMapping("/{memberId}/feeds")
    fun getFeeds(
        @PathVariable memberId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "10") minCommonFollowers: Long
    ): ResponseEntity<ApiResponse<PagedFeedsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val feeds = feedService.getFeedsByMemberWithRecommendations(
            memberId = memberId,
            pageable = pageable,
            minCommonFollowers = minCommonFollowers
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 목록 불러오기에 성공했습니다.",
                data = feeds
            )
        )
    }

    @GetMapping("/boards/{boardId}/feeds")
    fun getFeedsByBoard(
        @PathVariable boardId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember(required = false) member: Member?
    ): ResponseEntity<ApiResponse<PagedFeedsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val feeds = feedService.getFeedsByBoard(
            boardId = boardId,
            memberId = member?.id,
            pageable = pageable
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "보드별 피드 목록을 성공적으로 조회했습니다.",
                data = feeds
            )
        )
    }

    @GetMapping("/feeds/{feedId}")
    fun getFeedDetail(
        @PathVariable feedId: Long,
        @CurrentMember(required = false) member: Member?
    ): ResponseEntity<ApiResponse<FeedDetailResponse>> {
        val feedDetail = feedService.getFeedDetail(feedId, member?.id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 상세 정보를 성공적으로 조회했습니다.",
                data = feedDetail
            )
        )
    }

    @GetMapping("/feeds/{feedId}/comments")
    fun getComments(
        @PathVariable feedId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember(required = false) member: Member?
    ): ResponseEntity<ApiResponse<FeedCommentsResponse>> {
        val pageable = PageableValidator.validateAndCreate(
            page, 
            size, 
            "createdAt", 
            Sort.Direction.DESC
        )
        val comments = feedCommentService.getComments(feedId, member?.id, pageable)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 목록을 성공적으로 조회했습니다.",
                data = comments
            )
        )
    }
}
