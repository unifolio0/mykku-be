package com.example.mykku.dailymessage.exception

import com.example.mykku.common.exception.ErrorResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * DailyMessage 도메인 예외 처리 핸들러
 * DailyMessage 도메인에서 발생하는 모든 예외를 처리
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class DailyMessageExceptionHandler {

    @ExceptionHandler(DailyMessageException::class)
    fun handleDailyMessageException(exception: DailyMessageException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
