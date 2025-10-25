package com.example.mykku.admin.exception

import com.example.mykku.common.dto.ApiResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.example.mykku.admin"])
@Order(Ordered.HIGHEST_PRECEDENCE)
class AdminExceptionHandler {

    @ExceptionHandler(AdminException::class)
    fun handleAdminException(e: AdminException): ResponseEntity<ApiResponse<Nothing?>> {
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
