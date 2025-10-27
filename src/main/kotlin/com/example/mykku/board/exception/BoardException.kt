package com.example.mykku.board.exception

import com.example.mykku.common.exception.BaseDomainException

class BoardException(
    errorCode: BoardErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {
    
    companion object {
        fun boardNotFound(): BoardException = BoardException(BoardErrorCode.BOARD_NOT_FOUND)
        
        fun boardTitleTooLong(): BoardException = BoardException(BoardErrorCode.BOARD_TITLE_TOO_LONG)
        
        fun boardDuplicateTitle(): BoardException = BoardException(BoardErrorCode.BOARD_DUPLICATE_TITLE)
    }
}