package com.example.mykku.auth.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),

    OAUTH_USER_INFO_FAILED(HttpStatus.BAD_REQUEST, "사용자 정보를 가져오는데 실패했습니다"),
    OAUTH_EXTERNAL_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 오류가 발생했습니다"),
    OAUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    OAUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, "OAuth 접근이 거부되었습니다"),
    OAUTH_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "OAuth 서버에서 오류가 발생했습니다"),

    MOBILE_LOGIN_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "EMAIL 제공자는 모바일 로그인을 지원하지 않습니다")
}
