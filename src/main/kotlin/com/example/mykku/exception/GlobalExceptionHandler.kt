package com.example.mykku.exception

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
}
