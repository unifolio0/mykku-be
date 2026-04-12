package com.example.mykku.board.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class BoardErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    BOARD_NOT_FOUND("BO001", HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다")
}
