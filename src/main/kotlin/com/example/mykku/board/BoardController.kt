package com.example.mykku.board

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.feed.FeedService
import com.example.mykku.feed.dto.PagedFeedsResponse
import com.example.mykku.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/boards")
class BoardController(
    private val boardService: BoardService,
    private val feedService: FeedService
) {
    @PostMapping
    fun createBoard(
        @RequestBody request: CreateBoardRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CreateBoardResponse>> {
        val response = boardService.createBoard(
            request = request,
            memberId = member.id
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판이 성공적으로 생성되었습니다.",
                data = response
            )
        )
    }

    @PutMapping("/{id}")
    fun updateBoard(
        @PathVariable id: Long,
        @RequestBody request: UpdateBoardRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<UpdateBoardResponse>> {
        val response = boardService.updateBoard(
            request = request,
            boardId = id,
            memberId = member.id
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판이 성공적으로 수정되었습니다.",
                data = response
            )
        )
    }

    @GetMapping("/{boardId}/feeds")
    fun getFeedsByBoard(
        @PathVariable boardId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember(required = false) member: Member?
    ): ResponseEntity<ApiResponse<PagedFeedsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val feeds = feedService.getFeedsByBoard(
            boardId = boardId,
            memberId = member?.id,
            pageable = pageable
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "보드별 피드 목록을 성공적으로 조회했습니다.",
                data = feeds
            )
        )
    }
}
