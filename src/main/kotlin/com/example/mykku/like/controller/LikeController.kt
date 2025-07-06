package com.example.mykku.like.controller

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.like.dto.*
import com.example.mykku.like.service.LikeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class LikeController(
    private val likeService: LikeService
) {
    @GetMapping("/api/v1/boards/like")
    fun getLikedBoards(
        @RequestHeader("X-Member-Id") memberId: String
    ): ResponseEntity<ApiResponse<List<LikeBoardInfoResponse>>> {
        val response = likeService.getLikedBoards(memberId = memberId)
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
        @RequestHeader("X-Member-Id") memberId: String
    ): ResponseEntity<ApiResponse<LikeBoardResponse>> {
        val response = likeService.likeBoard(
            request = request,
            memberId = memberId
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    message = "게시판 즐겨찾기가 성공적으로 처리되었습니다.",
                    data = response
                )
            )
    }

    @DeleteMapping("/api/v1/board/unlike/{boardId}")
    fun unlikeBoard(
        @RequestHeader("X-Member-Id") memberId: String,
        @PathVariable boardId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeBoard(memberId = memberId, boardId = boardId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/v1/feed/like")
    fun likeFeed(
        @RequestBody request: LikeFeedRequest,
        @RequestHeader("X-Member-Id") memberId: String
    ): ResponseEntity<ApiResponse<LikeFeedResponse>> {
        val response = likeService.likeFeed(
            memberId = memberId,
            request = request
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    message = "피드 좋아요가 성공적으로 처리되었습니다.",
                    data = response
                )
            )
    }

    @DeleteMapping("/api/v1/feed/unlike/{feedId}")
    fun unlikeFeed(
        @RequestHeader("X-Member-Id") memberId: String,
        @PathVariable feedId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeed(memberId = memberId, feedId = feedId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/v1/daily-message-comment/like")
    fun likeDailyMessageComment(
        @RequestBody request: LikeDailyMessageCommentRequest,
        @RequestHeader("X-Member-Id") memberId: String
    ): ResponseEntity<ApiResponse<LikeDailyMessageCommentResponse>> {
        val response = likeService.likeDailyMessageComment(
            memberId = memberId,
            request = request
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                    data = response
                )
            )
    }

    @DeleteMapping("/api/v1/daily-message-comment/unlike/{dailyMessageCommentId}")
    fun unlikeDailyMessageComment(
        @RequestHeader("X-Member-Id") memberId: String,
        @PathVariable dailyMessageCommentId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeDailyMessageComment(memberId = memberId, dailyMessageCommentId = dailyMessageCommentId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/v1/comment/like")
    fun likeFeedComment(
        @RequestBody request: LikeFeedCommentRequest,
        @RequestHeader("X-Member-Id") memberId: String
    ): ResponseEntity<ApiResponse<LikeFeedCommentResponse>> {
        val response = likeService.likeFeedComment(
            memberId = memberId,
            request = request
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                    data = response
                )
            )
    }

    @DeleteMapping("/api/v1/comment/unlike/{feedCommentId}")
    fun unlikeFeedComment(
        @RequestHeader("X-Member-Id") memberId: String,
        @PathVariable feedCommentId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeedComment(memberId = memberId, feedCommentId = feedCommentId)
        return ResponseEntity.noContent().build()
    }
}
