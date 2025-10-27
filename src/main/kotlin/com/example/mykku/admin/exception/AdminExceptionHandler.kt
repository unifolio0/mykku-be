package com.example.mykku.admin.exception

import com.example.mykku.common.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.example.mykku.admin"])
@Order(Ordered.HIGHEST_PRECEDENCE)
class AdminExceptionHandler {

    private val logger = LoggerFactory.getLogger(AdminExceptionHandler::class.java)

    @ExceptionHandler(AdminException::class)
    fun handleAdminException(e: AdminException): ResponseEntity<ApiResponse<Nothing?>> {
        val requestId = MDC.get("req-id") ?: "unknown"
        logger.error("[$requestId] ${e::class.simpleName}: ${e.message}", e)
        
        val errorCode = e.errorCode as AdminErrorCode
        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse(
                    message = errorCode.message,
                    data = null
                )
            )
    }
}
