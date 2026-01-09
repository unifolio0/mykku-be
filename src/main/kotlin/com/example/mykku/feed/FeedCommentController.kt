package com.example.mykku.feed

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.feed.dto.CreateFeedCommentRequest
import com.example.mykku.feed.dto.SingleFeedCommentResponse
import com.example.mykku.feed.dto.UpdateFeedCommentRequest
import com.example.mykku.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/feeds")
class FeedCommentController(
    private val feedCommentService: FeedCommentService,
) {
    @PostMapping("/{feedId}/comments")
    fun createComment(
        @PathVariable feedId: Long,
        @RequestBody request: CreateFeedCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<SingleFeedCommentResponse>> {
        val comment = feedCommentService.createComment(
            feedId = feedId,
            memberId = member.id,
            request = request,
        )

        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글이 성공적으로 등록되었습니다.",
                data = comment,
            )
        )
    }

    @PutMapping("/comments/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody request: UpdateFeedCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<SingleFeedCommentResponse>> {
        val comment = feedCommentService.updateComment(
            commentId = commentId,
            memberId = member.id,
            request = request,
        )

        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글이 성공적으로 수정되었습니다.",
                data = comment,
            )
        )
    }

    @DeleteMapping("/comments/{commentId}")
    fun deleteComment(
        @PathVariable commentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        feedCommentService.deleteComment(
            commentId = commentId,
            memberId = member.id,
        )

        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글이 성공적으로 삭제되었습니다.",
                data = Unit,
            )
        )
    }
}
