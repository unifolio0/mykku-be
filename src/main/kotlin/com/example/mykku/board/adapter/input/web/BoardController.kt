package com.example.mykku.board.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.board.application.port.input.ListBoardsUseCase
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.feed.adapter.input.web.dto.PagedFeedsResponse
import com.example.mykku.feed.adapter.input.web.dto.PopularFeedsResponse
import com.example.mykku.feed.application.dto.GetPopularFeedsQuery
import com.example.mykku.feed.application.dto.ListFeedsQuery
import com.example.mykku.feed.application.port.input.GetPopularFeedsUseCase
import com.example.mykku.feed.application.port.input.ListFeedsUseCase
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/boards")
class BoardController(
    private val listBoardsUseCase: ListBoardsUseCase,
    private val listFeedsUseCase: ListFeedsUseCase,
    private val getPopularFeedsUseCase: GetPopularFeedsUseCase
) {
    @GetMapping
    fun getBoards(): ResponseEntity<ApiResponse<BoardResponses>> {
        val results = listBoardsUseCase.listBoards()
        val response = BoardResponses(
            boards = results.map { BoardResponse.from(it) }
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판 목록을 성공적으로 조회했습니다.",
                data = response
            )
        )
    }

    @GetMapping("/{boardId}/feeds")
    fun getFeedsByBoard(
        @PathVariable boardId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentMember(required = false) member: MemberJpaEntity?
    ): ResponseEntity<ApiResponse<PagedFeedsResponse>> {
        val pageable = PageableValidator.validateAndCreate(page, size)
        val query = ListFeedsQuery(
            boardId = boardId,
            memberId = member?.id,
            pageable = pageable
        )
        val result = listFeedsUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "보드별 피드 목록을 성공적으로 조회했습니다.",
                data = PagedFeedsResponse.from(result)
            )
        )
    }

    @GetMapping("/{boardId}/feeds/popular")
    fun getPopularFeedsByBoard(
        @PathVariable boardId: Long,
        @CurrentMember(required = false) member: MemberJpaEntity?
    ): ResponseEntity<ApiResponse<PopularFeedsResponse>> {
        val query = GetPopularFeedsQuery(boardId = boardId, memberId = member?.id)
        val result = getPopularFeedsUseCase.execute(query)
        return ResponseEntity.ok(
            ApiResponse(
                message = "인기 피드 목록을 성공적으로 조회했습니다.",
                data = PopularFeedsResponse.from(result)
            )
        )
    }
}
