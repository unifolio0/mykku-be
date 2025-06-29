package com.example.mykku.dailymessage.controller

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.dailymessage.service.DailyMessageCommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DailyMessageCommentController(
    private val dailyMessageCommentService: DailyMessageCommentService,
) {
    @PostMapping("/api/v1/daily-messages/{dailyMessageId}/comment")
    fun createComment(
        @PathVariable dailyMessageId: Long,
        @RequestHeader("X-Member-Id") memberId: String,
        @RequestBody request: CreateCommentRequest,
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val comment = dailyMessageCommentService.createComment(
            dailyMessageId = dailyMessageId,
            memberId = memberId,
            request = request,
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                message = "댓글이 성공적으로 등록되었습니다.",
                data = comment,
            )
        )
    }

    @PutMapping("/api/v1/daily-messages/comments/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestHeader("X-Member-Id") memberId: String,
        @RequestBody request: UpdateCommentRequest,
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val comment = dailyMessageCommentService.updateComment(
            commentId = commentId,
            memberId = memberId,
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
