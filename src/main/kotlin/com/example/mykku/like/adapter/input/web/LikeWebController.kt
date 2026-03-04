package com.example.mykku.like.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.like.adapter.input.web.dto.LikeBoardInfoWebResponse
import com.example.mykku.like.adapter.input.web.dto.LikeBoardWebResponse
import com.example.mykku.like.adapter.input.web.dto.LikeDailyMessageCommentWebResponse
import com.example.mykku.like.adapter.input.web.dto.LikeFeedCommentWebResponse
import com.example.mykku.like.adapter.input.web.dto.LikeFeedWebResponse
import com.example.mykku.like.application.dto.GetLikedBoardsQuery
import com.example.mykku.like.application.dto.LikeBoardCommand
import com.example.mykku.like.application.dto.LikeDailyMessageCommentCommand
import com.example.mykku.like.application.dto.LikeFeedCommand
import com.example.mykku.like.application.dto.LikeFeedCommentCommand
import com.example.mykku.like.application.dto.UnlikeBoardCommand
import com.example.mykku.like.application.dto.UnlikeDailyMessageCommentCommand
import com.example.mykku.like.application.dto.UnlikeFeedCommand
import com.example.mykku.like.application.dto.UnlikeFeedCommentCommand
import com.example.mykku.like.application.port.input.LikeBoardUseCase
import com.example.mykku.like.application.port.input.LikeDailyMessageCommentUseCase
import com.example.mykku.like.application.port.input.LikeFeedCommentUseCase
import com.example.mykku.like.application.port.input.LikeFeedUseCase
import com.example.mykku.member.domain.entity.Member
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
class LikeWebController(
    private val likeFeedUseCase: LikeFeedUseCase,
    private val likeFeedCommentUseCase: LikeFeedCommentUseCase,
    private val likeBoardUseCase: LikeBoardUseCase,
    private val likeDailyMessageCommentUseCase: LikeDailyMessageCommentUseCase
) {

    @GetMapping("/boards")
    fun getLikedBoards(
        @CurrentMember member: Member,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<LikeBoardInfoWebResponse>>> {
        PageableValidator.validateAndCreate(page, size, "createdAt", Sort.Direction.DESC)
        val query = GetLikedBoardsQuery(
            memberId = member.id.value,
            page = page,
            size = size
        )
        val result = likeBoardUseCase.getLikedBoards(query)
        val response = result.map { LikeBoardInfoWebResponse.from(it) }
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
    ): ResponseEntity<ApiResponse<LikeBoardWebResponse>> {
        val command = LikeBoardCommand(
            memberId = member.id.value,
            boardId = boardId
        )
        val result = likeBoardUseCase.likeBoard(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판 즐겨찾기가 성공적으로 처리되었습니다.",
                data = LikeBoardWebResponse.from(result, member.memberId)
            )
        )
    }

    @DeleteMapping("/boards/{boardId}")
    fun unlikeBoard(
        @PathVariable boardId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnlikeBoardCommand(
            memberId = member.id.value,
            boardId = boardId
        )
        likeBoardUseCase.unlikeBoard(command)
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
    ): ResponseEntity<ApiResponse<LikeFeedWebResponse>> {
        val command = LikeFeedCommand(
            memberId = member.id.value,
            feedId = feedId
        )
        val result = likeFeedUseCase.likeFeed(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "피드 좋아요가 성공적으로 처리되었습니다.",
                data = LikeFeedWebResponse.from(result, member.memberId)
            )
        )
    }

    @DeleteMapping("/feeds/{feedId}")
    fun unlikeFeed(
        @PathVariable feedId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnlikeFeedCommand(
            memberId = member.id.value,
            feedId = feedId
        )
        likeFeedUseCase.unlikeFeed(command)
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
    ): ResponseEntity<ApiResponse<LikeDailyMessageCommentWebResponse>> {
        val command = LikeDailyMessageCommentCommand(
            memberId = member.id.value,
            dailyMessageCommentId = dailyMessageCommentId
        )
        val result = likeDailyMessageCommentUseCase.likeDailyMessageComment(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                data = LikeDailyMessageCommentWebResponse.from(result, member.memberId)
            )
        )
    }

    @DeleteMapping("/daily-message-comments/{dailyMessageCommentId}")
    fun unlikeDailyMessageComment(
        @PathVariable dailyMessageCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnlikeDailyMessageCommentCommand(
            memberId = member.id.value,
            dailyMessageCommentId = dailyMessageCommentId
        )
        likeDailyMessageCommentUseCase.unlikeDailyMessageComment(command)
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
    ): ResponseEntity<ApiResponse<LikeFeedCommentWebResponse>> {
        val command = LikeFeedCommentCommand(
            memberId = member.id.value,
            feedCommentId = feedCommentId
        )
        val result = likeFeedCommentUseCase.likeFeedComment(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요가 성공적으로 처리되었습니다.",
                data = LikeFeedCommentWebResponse.from(result, member.memberId)
            )
        )
    }

    @DeleteMapping("/feed-comments/{feedCommentId}")
    fun unlikeFeedComment(
        @PathVariable feedCommentId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = UnlikeFeedCommentCommand(
            memberId = member.id.value,
            feedCommentId = feedCommentId
        )
        likeFeedCommentUseCase.unlikeFeedComment(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "댓글 좋아요 해제가 성공적으로 처리되었습니다.",
                data = Unit
            )
        )
    }
}
