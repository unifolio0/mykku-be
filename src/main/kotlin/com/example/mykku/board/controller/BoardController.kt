package com.example.mykku.board.controller

import com.example.mykku.board.dto.CreateBoardRequest
import com.example.mykku.board.dto.CreateBoardResponse
import com.example.mykku.board.dto.UpdateBoardRequest
import com.example.mykku.board.dto.UpdateBoardResponse
import com.example.mykku.board.service.BoardService
import com.example.mykku.common.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class BoardController(
    private val boardService: BoardService
) {
    @PostMapping("/api/v1/board")
    fun createBoard(
        @RequestHeader("X-Member-Id") memberId: String,
        @RequestBody request: CreateBoardRequest,
    ): ResponseEntity<ApiResponse<CreateBoardResponse>> {
        val response = boardService.createBoard(
            request = request,
            memberId = memberId
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
        @RequestHeader("X-Member-Id") memberId: String,
        @PathVariable id: Long,
        @RequestBody request: UpdateBoardRequest,
    ): ResponseEntity<ApiResponse<UpdateBoardResponse>> {
        val response = boardService.updateBoard(
            request = request,
            boardId = id,
            memberId = memberId
        )
        return ResponseEntity.ok(
            ApiResponse(
                message = "게시판이 성공적으로 수정되었습니다.",
                data = response
            )
        )
    }
}
