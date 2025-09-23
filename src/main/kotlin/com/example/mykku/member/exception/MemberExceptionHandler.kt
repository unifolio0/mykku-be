package com.example.mykku.member.exception

import com.example.mykku.common.exception.ErrorResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Member 도메인 예외 처리 핸들러
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class MemberExceptionHandler {

    @ExceptionHandler(MemberException::class)
    fun handleMemberException(exception: MemberException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
