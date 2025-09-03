package com.example.mykku.dailymessage

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DailyMessageCommentController(
    private val dailyMessageCommentService: DailyMessageCommentService,
) {
    @PostMapping("/api/v1/daily-messages/{dailyMessageId}/comment")
    fun createComment(
        @PathVariable dailyMessageId: Long,
        @RequestBody request: CreateCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val comment = dailyMessageCommentService.createComment(
            dailyMessageId = dailyMessageId,
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

    @PutMapping("/api/v1/daily-messages/comments/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody request: UpdateCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val comment = dailyMessageCommentService.updateComment(
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
}
