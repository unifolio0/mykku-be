package com.example.mykku.dailymessage.exception

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
class DailyMessageExceptionHandler {

    private val logger = LoggerFactory.getLogger(DailyMessageExceptionHandler::class.java)

    @ExceptionHandler(DailyMessageException::class)
    fun handleDailyMessageException(exception: DailyMessageException): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.code, exception.errorCode.message))
    }
}
