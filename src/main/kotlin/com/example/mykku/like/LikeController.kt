package com.example.mykku.like

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.like.dto.LikeBoardInfoResponse
import com.example.mykku.like.dto.LikeBoardRequest
import com.example.mykku.like.dto.LikeBoardResponse
import com.example.mykku.like.dto.LikeDailyMessageCommentRequest
import com.example.mykku.like.dto.LikeDailyMessageCommentResponse
import com.example.mykku.like.dto.LikeFeedCommentRequest
import com.example.mykku.like.dto.LikeFeedCommentResponse
import com.example.mykku.like.dto.LikeFeedRequest
import com.example.mykku.like.dto.LikeFeedResponse
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LikeController(
    private val likeService: LikeService
) {
    @GetMapping("/api/v1/boards/like")
    fun getLikedBoards(
        @CurrentMember member: Member,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<LikeBoardInfoResponse>>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )
        val response = likeService.getLikedBoards(memberId = member.id, pageable = pageable)
        return ResponseEntity.ok(
            ApiResponse(
                message = "즐겨찾기한 게시판 목록을 성공적으로 조회하였습니다.",
                data = response
            )
        )
    }

    @PostMapping("/api/v1/board/like")
    fun likeBoard(
        @RequestBody request: LikeBoardRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeBoardResponse>> {
        val response = likeService.likeBoard(
            request = request,
            memberId = member.id
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판 즐겨찾기가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/api/v1/board/unlike/{boardId}")
    fun unlikeBoard(
        @PathVariable boardId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeBoard(memberId = member.id, boardId = boardId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판 즐겨찾기 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/api/v1/feed/like")
    fun likeFeed(
        @RequestBody request: LikeFeedRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeFeedResponse>> {
        val response = likeService.likeFeed(
            memberId = member.id,
            request = request
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 좋아요가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/api/v1/feed/unlike/{feedId}")
    fun unlikeFeed(
        @PathVariable feedId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeed(memberId = member.id, feedId = feedId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/api/v1/daily-message-comment/like")
    fun likeDailyMessageComment(
        @RequestBody request: LikeDailyMessageCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeDailyMessageCommentResponse>> {
        val response = likeService.likeDailyMessageComment(
            memberId = member.id,
            request = request
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/api/v1/daily-message-comment/unlike/{dailyMessageCommentId}")
    fun unlikeDailyMessageComment(
        @PathVariable dailyMessageCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeDailyMessageComment(memberId = member.id, dailyMessageCommentId = dailyMessageCommentId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/api/v1/comment/like")
    fun likeFeedComment(
        @RequestBody request: LikeFeedCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeFeedCommentResponse>> {
        val response = likeService.likeFeedComment(
            memberId = member.id,
            request = request
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/api/v1/comment/unlike/{feedCommentId}")
    fun unlikeFeedComment(
        @PathVariable feedCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeedComment(memberId = member.id, feedCommentId = feedCommentId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }
}
