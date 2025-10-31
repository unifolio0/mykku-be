package com.example.mykku.email.exception

import com.example.mykku.common.exception.ErrorResponse
import com.example.mykku.common.exception.ExceptionLoggingSupport
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class EmailAuthExceptionHandler {

    private val logger = LoggerFactory.getLogger(EmailAuthExceptionHandler::class.java)

    @ExceptionHandler(EmailAuthException::class)
    fun handleEmailAuthException(exception: EmailAuthException): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.message))
    }
}
