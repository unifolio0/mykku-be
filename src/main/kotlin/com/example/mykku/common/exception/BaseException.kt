package com.example.mykku.common.exception

abstract class BaseException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    abstract val errorCode: DomainErrorCode
}