package com.example.mykku.board

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class BoardController(
    private val boardService: BoardService
) {
    @PostMapping("/api/v1/board")
    fun createBoard(
        @RequestBody request: CreateBoardRequest,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<CreateBoardResponse>> {
        val response = boardService.createBoard(
            request = request,
            memberId = member.id
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    message = "게시판이 성공적으로 생성되었습니다.",
                    data = response
                )
            )
    }

    @PutMapping("/api/v1/board/{id}")
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
}
