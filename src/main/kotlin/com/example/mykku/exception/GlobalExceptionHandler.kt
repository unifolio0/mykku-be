package com.example.mykku.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MykkuException::class)
    fun handleMykkuException(exception: MykkuException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.errorCode.status)
            .body(ErrorResponse(exception.errorCode.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("서버 오류가 발생했습니다. 관리자에게 문의해주세요."))
    }
}
