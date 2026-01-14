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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/daily-messages")
class DailyMessageCommentController(
    private val dailyMessageCommentService: DailyMessageCommentService,
) {
    @GetMapping("/{dailyMessageId}/comments")
    fun getComments(
        @PathVariable dailyMessageId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember(required = false) member: Member?
    ): ResponseEntity<ApiResponse<DailyMessageCommentsResponse>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "createdAt",
            Sort.Direction.DESC
        )
        val comments = dailyMessageCommentService.getComments(dailyMessageId, member?.id, pageable)

        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 목록을 성공적으로 조회했습니다.",
                data = comments,
            )
        )
    }

    @PostMapping("/{dailyMessageId}/comments")
    fun createComment(
        @PathVariable dailyMessageId: Long,
        @RequestBody request: CreateCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val comment = dailyMessageCommentService.createComment(
            dailyMessageId = dailyMessageId,
            member = member,
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
        @RequestBody request: UpdateCommentRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val comment = dailyMessageCommentService.updateComment(
            commentId = commentId,
            member = member,
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
        dailyMessageCommentService.deleteComment(
            commentId = commentId,
            member = member,
        )

        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글이 성공적으로 삭제되었습니다.",
                data = Unit,
            )
        )
    }
}
