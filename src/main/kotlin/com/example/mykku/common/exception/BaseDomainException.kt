package com.example.mykku.common.exception

abstract class BaseDomainException(
    override val errorCode: DomainErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseException(
    message = additionalMessage ?: errorCode.message,
    cause = cause
)