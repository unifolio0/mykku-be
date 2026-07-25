package com.example.mykku.common.exception

import org.redisson.client.RedisException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.dao.QueryTimeoutException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class BaseExceptionHandler {

    private val logger = LoggerFactory.getLogger(BaseExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        val errorMessage = exception.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: CommonErrorCode.INVALID_INPUT.message }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(CommonErrorCode.INVALID_INPUT.code, errorMessage))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        exception: HttpMessageNotReadableException
    ): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(CommonErrorCode.INVALID_INPUT.code, CommonErrorCode.INVALID_INPUT.message))
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(exception: BaseException): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return ResponseEntity
            .status(exception.errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(exception.errorCode.code, exception.errorCode.message))
    }

    @ExceptionHandler(PessimisticLockingFailureException::class, QueryTimeoutException::class)
    fun handleLockConflictException(exception: Exception): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return errorResponseOf(CommonErrorCode.RESOURCE_LOCK_CONFLICT)
    }

    @ExceptionHandler(RedisException::class)
    fun handleRedisException(exception: RedisException): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return errorResponseOf(CommonErrorCode.REDIS_CONNECTION_FAILURE)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        ExceptionLoggingSupport.logException(logger, exception)

        return errorResponseOf(CommonErrorCode.INTERNAL_SERVER_ERROR)
    }

    private fun errorResponseOf(errorCode: CommonErrorCode): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(errorCode.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(errorCode.code, errorCode.message))
    }
}
