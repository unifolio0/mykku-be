package com.example.mykku.dailymessage

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.DailyMessageCommentsResponse
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DailyMessageCommentController(
    private val dailyMessageCommentService: DailyMessageCommentService,
) {
    @GetMapping("/api/v1/daily-messages/{dailyMessageId}/comments")
    fun getComments(
        @PathVariable dailyMessageId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<DailyMessageCommentsResponse>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )
        val comments = dailyMessageCommentService.getComments(dailyMessageId, pageable)

        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 목록을 성공적으로 조회했습니다.",
                data = comments,
            )
        )
    }

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

    @DeleteMapping("/api/v1/daily-messages/comments/{commentId}")
    fun deleteComment(
        @PathVariable commentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        dailyMessageCommentService.deleteComment(
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
