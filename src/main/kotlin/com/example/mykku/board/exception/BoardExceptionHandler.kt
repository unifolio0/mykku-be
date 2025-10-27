package com.example.mykku.board.exception

import com.example.mykku.common.exception.ErrorResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Board 도메인 예외 처리 핸들러
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class BoardExceptionHandler {

    private val logger = LoggerFactory.getLogger(BoardExceptionHandler::class.java)

    @ExceptionHandler(BoardException::class)
    fun handleBoardException(exception: BoardException): ResponseEntity<ErrorResponse> {
        val requestId = MDC.get("req-id") ?: "unknown"
        logger.error("[$requestId] ${exception::class.simpleName}: ${exception.message}", exception)
        
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
