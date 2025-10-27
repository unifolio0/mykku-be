package com.example.mykku.image.exception

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
 * Image 도메인 예외 처리 핸들러
 * Image 도메인에서 발생하는 모든 예외를 처리
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ImageExceptionHandler {

    private val logger = LoggerFactory.getLogger(ImageExceptionHandler::class.java)

    @ExceptionHandler(ImageException::class)
    fun handleImageException(exception: ImageException): ResponseEntity<ErrorResponse> {
        val requestId = MDC.get("req-id") ?: "unknown"
        logger.error("[$requestId] ${exception::class.simpleName}: ${exception.message}", exception)
        
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
