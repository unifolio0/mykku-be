package com.example.mykku.scrap.exception

import com.example.mykku.common.exception.ErrorResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ScrapExceptionHandler {

    @ExceptionHandler(ScrapException::class)
    fun handleScrapException(exception: ScrapException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
