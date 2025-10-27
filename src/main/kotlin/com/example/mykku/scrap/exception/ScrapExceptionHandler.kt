package com.example.mykku.scrap.exception

import com.example.mykku.common.exception.ErrorResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ScrapExceptionHandler {

    private val logger = LoggerFactory.getLogger(ScrapExceptionHandler::class.java)

    @ExceptionHandler(ScrapException::class)
    fun handleScrapException(exception: ScrapException): ResponseEntity<ErrorResponse> {
        val requestId = MDC.get("req-id") ?: "unknown"
        logger.error("[$requestId] ${exception::class.simpleName}: ${exception.message}", exception)
        
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
