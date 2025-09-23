package com.example.mykku.common.exception

/**
 * 도메인별 예외의 추상 클래스
 * 각 도메인은 이 클래스를 상속받아 자체 예외 클래스를 정의합니다.
 */
abstract class BaseDomainException(
    override val errorCode: DomainErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseException(
    message = additionalMessage ?: errorCode.message,
    cause = cause
)