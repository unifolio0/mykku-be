package com.example.mykku.common.exception

/**
 * 모든 커스텀 예외의 기본 클래스
 */
abstract class BaseException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    abstract val errorCode: DomainErrorCode
}