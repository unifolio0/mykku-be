package com.example.mykku.common.exception

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 모든 BaseException을 처리하는 공통 핸들러
 * 도메인별 핸들러에서 처리되지 않은 예외를 처리
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class BaseExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(exception: BaseException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(CommonErrorCode.INTERNAL_SERVER_ERROR.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR.message))
    }
}
