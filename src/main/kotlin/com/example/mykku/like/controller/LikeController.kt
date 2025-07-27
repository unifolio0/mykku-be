package com.example.mykku.like.controller

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.like.dto.*
import com.example.mykku.like.service.LikeService
import com.example.mykku.member.domain.Member
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class LikeController(
    private val likeService: LikeService
) {
    @GetMapping("/api/v1/boards/like")
    fun getLikedBoards(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<LikeBoardInfoResponse>>> {
        val response = likeService.getLikedBoards(memberId = member.id)
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
        @PathVariable boardId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeBoard(memberId = member.id, boardId = boardId)
        return ResponseEntity.noContent().build()
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
        @PathVariable feedId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeed(memberId = member.id, feedId = feedId)
        return ResponseEntity.noContent().build()
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
        @PathVariable dailyMessageCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeDailyMessageComment(memberId = member.id, dailyMessageCommentId = dailyMessageCommentId)
        return ResponseEntity.noContent().build()
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
        @PathVariable feedCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeedComment(memberId = member.id, feedCommentId = feedCommentId)
        return ResponseEntity.noContent().build()
    }
}
