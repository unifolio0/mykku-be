package com.example.mykku.board.exception

import com.example.mykku.common.exception.BaseDomainException

class BoardException(
    errorCode: BoardErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun boardNotFound(): BoardException = BoardException(BoardErrorCode.BOARD_NOT_FOUND)
    }
}
