package com.example.mykku.common.exception

import org.springframework.http.HttpStatus

/**
 * 모든 도메인별 에러 코드가 구현해야 하는 인터페이스
 * 각 도메인은 이 인터페이스를 구현한 enum을 정의해야 합니다.
 */
interface DomainErrorCode {
    val status: HttpStatus
    val message: String
    val code: String
        get() = name

    val name: String
}