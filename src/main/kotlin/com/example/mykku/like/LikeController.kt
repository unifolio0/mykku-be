package com.example.mykku.like

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.like.dto.LikeBoardInfoResponse
import com.example.mykku.like.dto.LikeBoardResponse
import com.example.mykku.like.dto.LikeDailyMessageCommentResponse
import com.example.mykku.like.dto.LikeFeedCommentResponse
import com.example.mykku.like.dto.LikeFeedResponse
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/likes")
class LikeController(
    private val likeService: LikeService
) {
    @GetMapping("/boards")
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
        val response = likeService.getLikedBoards(member = member, pageable = pageable)
        return ResponseEntity.ok(
            ApiResponse(
                message = "즐겨찾기한 게시판 목록을 성공적으로 조회하였습니다.",
                data = response
            )
        )
    }

    @PostMapping("/boards/{boardId}")
    fun likeBoard(
        @PathVariable boardId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeBoardResponse>> {
        val response = likeService.likeBoard(
            boardId = boardId,
            member = member
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판 즐겨찾기가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/boards/{boardId}")
    fun unlikeBoard(
        @PathVariable boardId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeBoard(member = member, boardId = boardId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판 즐겨찾기 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/feeds/{feedId}")
    fun likeFeed(
        @PathVariable feedId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeFeedResponse>> {
        val response = likeService.likeFeed(
            member = member,
            feedId = feedId
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 좋아요가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/feeds/{feedId}")
    fun unlikeFeed(
        @PathVariable feedId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeed(member = member, feedId = feedId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/daily-message-comments/{dailyMessageCommentId}")
    fun likeDailyMessageComment(
        @PathVariable dailyMessageCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeDailyMessageCommentResponse>> {
        val response = likeService.likeDailyMessageComment(
            member = member,
            dailyMessageCommentId = dailyMessageCommentId
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/daily-message-comments/{dailyMessageCommentId}")
    fun unlikeDailyMessageComment(
        @PathVariable dailyMessageCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeDailyMessageComment(member = member, dailyMessageCommentId = dailyMessageCommentId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }

    @PostMapping("/feed-comments/{feedCommentId}")
    fun likeFeedComment(
        @PathVariable feedCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<LikeFeedCommentResponse>> {
        val response = likeService.likeFeedComment(
            member = member,
            feedCommentId = feedCommentId
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                data = response
            )
        )
    }

    @DeleteMapping("/feed-comments/{feedCommentId}")
    fun unlikeFeedComment(
        @PathVariable feedCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        likeService.unlikeFeedComment(member = member, feedCommentId = feedCommentId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }
}
