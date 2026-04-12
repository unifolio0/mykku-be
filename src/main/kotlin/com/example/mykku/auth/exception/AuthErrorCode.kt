package com.example.mykku.auth.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    UNAUTHORIZED("AU201", HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INVALID_TOKEN("AU202", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    OAUTH_INVALID_TOKEN("AU203", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    OAUTH_ACCESS_DENIED("AU204", HttpStatus.FORBIDDEN, "OAuth 접근이 거부되었습니다"),

    OAUTH_USER_INFO_FAILED("AU101", HttpStatus.BAD_REQUEST, "사용자 정보를 가져오는데 실패했습니다"),
    MOBILE_LOGIN_NOT_SUPPORTED("AU102", HttpStatus.BAD_REQUEST, "EMAIL 제공자는 모바일 로그인을 지원하지 않습니다"),

    OAUTH_EXTERNAL_SERVICE_ERROR("AU401", HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 오류가 발생했습니다"),
    OAUTH_SERVER_ERROR("AU402", HttpStatus.SERVICE_UNAVAILABLE, "OAuth 서버에서 오류가 발생했습니다")
}
