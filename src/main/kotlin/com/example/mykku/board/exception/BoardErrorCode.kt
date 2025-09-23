package com.example.mykku.board.exception

import com.example.mykku.board.domain.Board
import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

/**
 * Board 도메인 에러 코드
 */
enum class BoardErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다"),
    BOARD_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "게시판 제목은 ${Board.TITLE_MAX_LENGTH}자 이하여야 합니다"),
    BOARD_DUPLICATE_TITLE(HttpStatus.BAD_REQUEST, "이미 존재하는 게시판 제목입니다")
}